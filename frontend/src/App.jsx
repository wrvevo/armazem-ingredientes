import { useState } from 'react';
import { Login } from './Login';
import { Painel } from './Painel';
import { lerSessao, limparSessao } from './api';

/**
 * Alterna entre a tela de login e o painel autenticado.
 *
 * @returns {JSX.Element} árvore da aplicação
 */
export function App() {
  const [sessao, setSessao] = useState(() => lerSessao());

  /**
   * Encerra a sessão local e volta para o login.
   */
  function sair() {
    limparSessao();
    setSessao(null);
  }

  if (!sessao) {
    return <Login onSucesso={setSessao} />;
  }
  return <Painel sessao={sessao} onSair={sair} />;
}
