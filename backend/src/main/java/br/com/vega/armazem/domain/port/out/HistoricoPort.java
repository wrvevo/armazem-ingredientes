package br.com.vega.armazem.domain.port.out;

import br.com.vega.armazem.domain.model.Movimentacao;

import java.util.List;

/**
 * Porta de saída do histórico de entradas e saídas.
 */
public interface HistoricoPort {

    /**
     * Acrescenta uma movimentação. A chamada participa da mesma transação do estoque.
     *
     * @param movimentacao lançamento ainda sem identificador
     * @return lançamento persistido
     */
    Movimentacao registrar(Movimentacao movimentacao);

    /**
     * Lista o histórico já ordenado pelos campos validados pela aplicação.
     *
     * @param campo campo de ordenação principal, dataHora ou compartimentoId
     * @param direcao ASC ou DESC
     * @return movimentações na ordem pedida
     */
    List<Movimentacao> listar(String campo, String direcao);
}
