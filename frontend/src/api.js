const CHAVE = 'armazem.sessao';

/**
 * Erro de API com o status HTTP preservado para a tela decidir o que fazer.
 */
export class ErroApi extends Error {
  /**
   * Cria o erro exibido no painel.
   *
   * @param {string} mensagem texto devolvido pela API ou uma mensagem local
   * @param {number} status código HTTP
   */
  constructor(mensagem, status) {
    super(mensagem);
    this.name = 'ErroApi';
    this.status = status;
  }
}

/**
 * Lê a sessão guardada nesta aba.
 *
 * @returns {{token: string, usuario: string, papeis: string[]} | null} sessão ou nulo
 */
export function lerSessao() {
  const texto = sessionStorage.getItem(CHAVE);
  return texto ? JSON.parse(texto) : null;
}

/**
 * Guarda o token e os dados exibidos no cabeçalho.
 *
 * @param {{token: string, usuario: string, papeis: string[]}} sessao sessão autenticada
 */
export function guardarSessao(sessao) {
  sessionStorage.setItem(CHAVE, JSON.stringify(sessao));
}

/**
 * Remove a sessão local ao sair ou quando o token expira.
 */
export function limparSessao() {
  sessionStorage.removeItem(CHAVE);
}

/**
 * Envia uma requisição ao backend pelo prefixo /api.
 *
 * @param {string} caminho caminho da API, começando com /
 * @param {{method?: string, body?: unknown}} opcoes método e corpo opcionais
 * @returns {Promise<any>} corpo JSON da resposta
 */
export async function requisicao(caminho, opcoes = {}) {
  const sessao = lerSessao();
  const cabecalhos = {};
  if (opcoes.body !== undefined) {
    cabecalhos['Content-Type'] = 'application/json';
  }
  if (sessao?.token) {
    cabecalhos.Authorization = `Bearer ${sessao.token}`;
  }
  const resposta = await fetch(`/api${caminho}`, {
    method: opcoes.method || 'GET',
    headers: cabecalhos,
    body: opcoes.body !== undefined ? JSON.stringify(opcoes.body) : undefined,
  });
  const texto = await resposta.text();
  const corpo = texto ? JSON.parse(texto) : null;
  if (!resposta.ok) {
    throw new ErroApi(corpo?.mensagem || 'Não foi possível concluir a operação.', resposta.status);
  }
  return corpo;
}

/**
 * Autentica o operador e devolve o token.
 *
 * @param {string} usuario nome de login
 * @param {string} senha senha informada
 * @returns {Promise<object>} resposta do login
 */
export function entrar(usuario, senha) {
  return requisicao('/auth/login', { method: 'POST', body: { usuario, senha } });
}

/**
 * Lista os ingredientes cadastrados.
 *
 * @returns {Promise<object[]>} saldos
 */
export function listarIngredientes() {
  return requisicao('/ingredientes');
}

/**
 * Registra uma entrada de ingrediente.
 *
 * @param {object} dados corpo do cadastro
 * @returns {Promise<object>} ingrediente criado
 */
export function cadastrarIngrediente(dados) {
  return requisicao('/ingredientes', { method: 'POST', body: dados });
}

/**
 * Registra a saída de um ingrediente.
 *
 * @param {number} id identificador do ingrediente
 * @param {object} dados quantidade e responsável
 * @returns {Promise<object>} saldo restante
 */
export function registrarSaida(id, dados) {
  return requisicao(`/ingredientes/${id}/saida`, { method: 'POST', body: dados });
}

/**
 * Consulta o volume total por tipo.
 *
 * @returns {Promise<object[]>} totais
 */
export function consultarVolume() {
  return requisicao('/ingredientes/volume');
}

/**
 * Lista o estado dos compartimentos.
 *
 * @returns {Promise<object[]>} compartimentos
 */
export function listarCompartimentos() {
  return requisicao('/compartimentos');
}

/**
 * Consulta compartimentos com espaço livre suficiente.
 *
 * @param {string} quantidade quantidade desejada
 * @param {string} tipo tipo desejado
 * @returns {Promise<object[]>} compartimentos compatíveis
 */
export function consultarDisponiveis(quantidade, tipo) {
  return requisicao(`/compartimentos/disponiveis?quantidade=${encodeURIComponent(quantidade)}&tipo=${encodeURIComponent(tipo)}`);
}

/**
 * Consulta compartimentos com saldo para retirada.
 *
 * @param {string} tipo tipo desejado
 * @returns {Promise<object[]>} compartimentos com estoque
 */
export function consultarVenda(tipo) {
  return requisicao(`/compartimentos/disponiveis-para-venda?tipo=${encodeURIComponent(tipo)}`);
}

/**
 * Lista o histórico na ordem pedida.
 *
 * @param {string} sortBy date ou compartimento
 * @param {string} order asc ou desc
 * @returns {Promise<object[]>} movimentações
 */
export function consultarHistorico(sortBy, order) {
  return requisicao(`/historico?sortBy=${encodeURIComponent(sortBy)}&order=${encodeURIComponent(order)}`);
}
