import { useState } from 'react';
import { entrar, guardarSessao } from './api';

/**
 * Tela de login protegida pelo Spring Security.
 *
 * @param {{onSucesso: (sessao: object) => void}} props callback após autenticar
 * @returns {JSX.Element} formulário de acesso
 */
export function Login({ onSucesso }) {
  const [usuario, setUsuario] = useState('admin');
  const [senha, setSenha] = useState('admin123');
  const [erro, setErro] = useState('');
  const [enviando, setEnviando] = useState(false);

  /**
   * Envia as credenciais e guarda o token da sessão.
   *
   * @param {SubmitEvent} evento submissão do formulário
   */
  async function enviar(evento) {
    evento.preventDefault();
    setEnviando(true);
    setErro('');
    try {
      const resposta = await entrar(usuario.trim(), senha);
      const sessao = { token: resposta.token, usuario: resposta.usuario, papeis: resposta.papeis };
      guardarSessao(sessao);
      onSucesso(sessao);
    } catch (falha) {
      setErro(falha.message);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <main className="login">
      <section className="cartao login-cartao">
        <p className="selo">Vega · fábrica de alimentos</p>
        <h1>Armazém de ingredientes</h1>
        <p className="subtítulo">Entre para movimentar os compartimentos C1 a C5.</p>
        <form onSubmit={enviar}>
          <label>
            Usuário
            <input value={usuario} onChange={(evento) => setUsuario(evento.target.value)} autoComplete="username" />
          </label>
          <label>
            Senha
            <input type="password" value={senha} onChange={(evento) => setSenha(evento.target.value)} autoComplete="current-password" />
          </label>
          {erro && <p className="alerta">{erro}</p>}
          <button type="submit" disabled={enviando}>{enviando ? 'Entrando...' : 'Entrar'}</button>
        </form>
        <dl className="credenciais">
          <div><dt>Administrador</dt><dd>admin / admin123</dd></div>
          <div><dt>Operador</dt><dd>pedro / pedro123</dd></div>
        </dl>
      </section>
    </main>
  );
}
