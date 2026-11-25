import { useEffect, useState } from 'react';
import {
  cadastrarIngrediente,
  consultarDisponiveis,
  consultarHistorico,
  consultarVenda,
  consultarVolume,
  ErroApi,
  listarCompartimentos,
  listarIngredientes,
  registrarSaida,
} from './api';

const TIPOS = [
  { valor: 'SECO', rotulo: 'Secos' },
  { valor: 'LIQUIDO', rotulo: 'Líquidos' },
  { valor: 'REFRIGERADO', rotulo: 'Refrigerados' },
];

/**
 * Formata quantidade com três casas e vírgula decimal.
 *
 * @param {number | string | null} valor quantidade recebida
 * @returns {string} texto formatado ou traço
 */
function formatar(valor) {
  if (valor === null || valor === undefined || valor === '') {
    return '—';
  }
  return Number(valor).toLocaleString('pt-BR', { minimumFractionDigits: 3, maximumFractionDigits: 3 });
}

/**
 * Painel autenticado para testar cadastro, saída, consultas e histórico.
 *
 * @param {{sessao: {usuario: string, papeis: string[]}, onSair: () => void}} props sessão e saída
 * @returns {JSX.Element} painel operacional
 */
export function Painel({ sessao, onSair }) {
  const [volumes, setVolumes] = useState([]);
  const [compartimentos, setCompartimentos] = useState([]);
  const [ingredientes, setIngredientes] = useState([]);
  const [historico, setHistorico] = useState([]);
  const [sortBy, setSortBy] = useState('date');
  const [order, setOrder] = useState('desc');
  const [aviso, setAviso] = useState('');
  const [sucesso, setSucesso] = useState('');
  const [disponiveis, setDisponiveis] = useState(null);
  const [venda, setVenda] = useState(null);
  const [entrada, setEntrada] = useState({
    nome: 'Farinha de Trigo',
    tipo: 'SECOS',
    quantidade: '400',
    compartimentoId: '1',
    responsavel: sessao.usuario,
  });
  const [saida, setSaida] = useState({ ingredienteId: '', quantidade: '10', responsavel: sessao.usuario });
  const [consulta, setConsulta] = useState({ quantidade: '100', tipo: 'SECO' });
  const [tipoVenda, setTipoVenda] = useState('SECO');

  /**
   * Recarrega volumes, compartimentos, ingredientes e histórico.
   *
   * @param {string} ordenacaoCampo campo de ordenação do histórico
   * @param {string} ordenacaoDirecao direção da ordenação
   */
  async function carregar(ordenacaoCampo = sortBy, ordenacaoDirecao = order) {
    const [volumesAtuais, compartimentosAtuais, ingredientesAtuais, historicoAtual] = await Promise.all([
      consultarVolume(),
      listarCompartimentos(),
      listarIngredientes(),
      consultarHistorico(ordenacaoCampo, ordenacaoDirecao),
    ]);
    setVolumes(volumesAtuais);
    setCompartimentos(compartimentosAtuais);
    setIngredientes(ingredientesAtuais);
    setHistorico(historicoAtual);
  }

  /**
   * Trata erro de API e devolve à tela de login quando o token expira.
   *
   * @param {unknown} falha erro capturado
   */
  function tratar(falha) {
    if (falha instanceof ErroApi && falha.status === 401) {
      onSair();
      return;
    }
    setSucesso('');
    setAviso(falha.message || 'Falha inesperada.');
  }

  useEffect(() => {
    carregar().catch(tratar);
  }, []);

  /**
   * Envia o cadastro de entrada e atualiza o painel.
   *
   * @param {SubmitEvent} evento submissão do formulário
   */
  async function enviarEntrada(evento) {
    evento.preventDefault();
    setAviso('');
    try {
      const criado = await cadastrarIngrediente({
        ...entrada,
        quantidade: Number(entrada.quantidade),
        compartimentoId: Number(entrada.compartimentoId),
      });
      setSucesso(`${criado.nome} entrou em ${criado.compartimentoCodigo}.`);
      await carregar();
    } catch (falha) {
      tratar(falha);
    }
  }

  /**
   * Envia a saída do ingrediente selecionado.
   *
   * @param {SubmitEvent} evento submissão do formulário
   */
  async function enviarSaida(evento) {
    evento.preventDefault();
    setAviso('');
    try {
      const atualizado = await registrarSaida(Number(saida.ingredienteId), {
        quantidade: Number(saida.quantidade),
        responsavel: saida.responsavel,
      });
      setSucesso(`Saída registrada. Saldo de ${atualizado.nome}: ${formatar(atualizado.quantidade)}.`);
      await carregar();
    } catch (falha) {
      tratar(falha);
    }
  }

  /**
   * Busca compartimentos capazes de receber a quantidade informada.
   *
   * @param {SubmitEvent} evento submissão do formulário
   */
  async function buscarDisponiveis(evento) {
    evento.preventDefault();
    setAviso('');
    try {
      setDisponiveis(await consultarDisponiveis(consulta.quantidade, consulta.tipo));
    } catch (falha) {
      tratar(falha);
    }
  }

  /**
   * Busca compartimentos com saldo do tipo escolhido.
   *
   * @param {SubmitEvent} evento submissão do formulário
   */
  async function buscarVenda(evento) {
    evento.preventDefault();
    setAviso('');
    try {
      setVenda(await consultarVenda(tipoVenda));
    } catch (falha) {
      tratar(falha);
    }
  }

  /**
   * Recarrega o histórico com a ordenação escolhida.
   *
   * @param {string} campo date ou compartimento
   * @param {string} direcao asc ou desc
   */
  async function ordenar(campo, direcao) {
    setSortBy(campo);
    setOrder(direcao);
    try {
      setHistorico(await consultarHistorico(campo, direcao));
    } catch (falha) {
      tratar(falha);
    }
  }

  const comSaldo = ingredientes.filter((item) => Number(item.quantidade) > 0);

  return (
    <div className="app">
      <header className="topo">
        <div>
          <p className="selo">Painel de testes</p>
          <h1>Armazém de ingredientes</h1>
        </div>
        <div className="usuario">
          <span>{sessao.usuario}</span>
          <small>{(sessao.papeis || []).join(', ')}</small>
          <button type="button" className="secundario" onClick={onSair}>Sair</button>
        </div>
      </header>

      {aviso && <p className="alerta">{aviso}</p>}
      {sucesso && <p className="ok">{sucesso}</p>}

      <section>
        <h2>Volume por tipo</h2>
        <div className="grades">
          {volumes.map((volume) => (
            <article key={volume.type} className="cartao metrica">
              <span>{volume.type}</span>
              <strong>{formatar(volume.totalQuantity)}</strong>
            </article>
          ))}
        </div>
      </section>

      <section>
        <h2>Compartimentos</h2>
        <div className="grades compartimentos">
          {compartimentos.map((item) => (
            <article key={item.codigo} className="cartao">
              <header>
                <strong>{item.codigo}</strong>
                <span>{item.tipoAtual || 'vazio'}</span>
              </header>
              <p>Saldo {formatar(item.quantidadeAtual)} {item.unidade || ''}</p>
              <p>Livre {formatar(item.espacoLivre)} / capacidade {formatar(item.capacidadeMaxima)}</p>
              <small>Último seco: {item.dataUltimoArmazenamentoSeco || 'nunca'}</small>
            </article>
          ))}
        </div>
      </section>

      <section className="duas">
        <form className="cartao" onSubmit={enviarEntrada}>
          <h2>Entrada</h2>
          <label>Nome<input value={entrada.nome} onChange={(evento) => setEntrada({ ...entrada, nome: evento.target.value })} /></label>
          <label>Tipo
            <select value={entrada.tipo} onChange={(evento) => setEntrada({ ...entrada, tipo: evento.target.value })}>
              <option value="SECOS">SECOS</option>
              <option value="LIQUIDO">LIQUIDO</option>
              <option value="REFRIGERADO">REFRIGERADO</option>
            </select>
          </label>
          <label>Quantidade<input value={entrada.quantidade} onChange={(evento) => setEntrada({ ...entrada, quantidade: evento.target.value })} /></label>
          <label>Compartimento
            <select value={entrada.compartimentoId} onChange={(evento) => setEntrada({ ...entrada, compartimentoId: evento.target.value })}>
              {compartimentos.map((item) => <option key={item.id} value={item.id}>{item.codigo}</option>)}
            </select>
          </label>
          <label>Responsável<input value={entrada.responsavel} onChange={(evento) => setEntrada({ ...entrada, responsavel: evento.target.value })} /></label>
          <button type="submit">Registrar entrada</button>
        </form>

        <form className="cartao" onSubmit={enviarSaida}>
          <h2>Saída</h2>
          <label>Ingrediente
            <select value={saida.ingredienteId} onChange={(evento) => setSaida({ ...saida, ingredienteId: evento.target.value })} required>
              <option value="">Selecione</option>
              {comSaldo.map((item) => (
                <option key={item.id} value={item.id}>
                  {item.nome} · {item.compartimentoCodigo} · {formatar(item.quantidade)}
                </option>
              ))}
            </select>
          </label>
          <label>Quantidade<input value={saida.quantidade} onChange={(evento) => setSaida({ ...saida, quantidade: evento.target.value })} /></label>
          <label>Responsável<input value={saida.responsavel} onChange={(evento) => setSaida({ ...saida, responsavel: evento.target.value })} /></label>
          <button type="submit">Registrar saída</button>
        </form>
      </section>

      <section className="duas">
        <form className="cartao" onSubmit={buscarDisponiveis}>
          <h2>Espaço disponível</h2>
          <label>Quantidade<input value={consulta.quantidade} onChange={(evento) => setConsulta({ ...consulta, quantidade: evento.target.value })} /></label>
          <label>Tipo
            <select value={consulta.tipo} onChange={(evento) => setConsulta({ ...consulta, tipo: evento.target.value })}>
              {TIPOS.map((tipo) => <option key={tipo.valor} value={tipo.valor}>{tipo.rotulo}</option>)}
            </select>
          </label>
          <button type="submit">Consultar</button>
          <ListaResultado itens={disponiveis} vazio="Nenhum compartimento comporta essa quantidade." />
        </form>

        <form className="cartao" onSubmit={buscarVenda}>
          <h2>Disponíveis para venda</h2>
          <label>Tipo
            <select value={tipoVenda} onChange={(evento) => setTipoVenda(evento.target.value)}>
              {TIPOS.map((tipo) => <option key={tipo.valor} value={tipo.valor}>{tipo.rotulo}</option>)}
            </select>
          </label>
          <button type="submit">Consultar</button>
          <ListaResultado itens={venda} vazio="Nenhum compartimento tem saldo desse tipo." />
        </form>
      </section>

      <section className="cartao">
        <div className="linha">
          <h2>Histórico</h2>
          <label>Ordenar
            <select value={sortBy} onChange={(evento) => ordenar(evento.target.value, order)}>
              <option value="date">Data</option>
              <option value="compartimento">Compartimento</option>
            </select>
          </label>
          <label>Ordem
            <select value={order} onChange={(evento) => ordenar(sortBy, evento.target.value)}>
              <option value="desc">Decrescente</option>
              <option value="asc">Crescente</option>
            </select>
          </label>
        </div>
        <div className="tabela-wrap">
          <table>
            <thead>
              <tr>
                <th>Data</th>
                <th>Operação</th>
                <th>Ingrediente</th>
                <th>Tipo</th>
                <th>Qtd.</th>
                <th>Compartimento</th>
                <th>Responsável</th>
              </tr>
            </thead>
            <tbody>
              {historico.length === 0 && (
                <tr><td colSpan="7">Nenhuma movimentação ainda.</td></tr>
              )}
              {historico.map((item) => (
                <tr key={item.id}>
                  <td>{item.dataHora?.replace('T', ' ')}</td>
                  <td>{item.operacao}</td>
                  <td>{item.nomeIngrediente}</td>
                  <td>{item.tipo}</td>
                  <td>{formatar(item.quantidade)}</td>
                  <td>{item.compartimentoCodigo}</td>
                  <td>{item.responsavel}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}

/**
 * Mostra o resultado de uma consulta de compartimentos.
 *
 * @param {{itens: object[] | null, vazio: string}} props lista e texto quando não há resultado
 * @returns {JSX.Element | null} lista ou mensagem vazia
 */
function ListaResultado({ itens, vazio }) {
  if (!itens) {
    return null;
  }
  if (itens.length === 0) {
    return <p className="vazio">{vazio}</p>;
  }
  return (
    <ul className="resultado">
      {itens.map((item) => (
        <li key={item.codigo}>
          {item.codigo} · saldo {formatar(item.quantidadeAtual)} · livre {formatar(item.espacoLivre)} {item.unidade}
        </li>
      ))}
    </ul>
  );
}
