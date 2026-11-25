package br.com.vega.armazem.domain.model;

/**
 * Operações que obrigatoriamente geram uma linha no histórico.
 */
public enum Operacao {

    ENTRADA("Entrada de ingrediente no compartimento"),
    SAIDA("Saída de ingrediente do compartimento");

    private final String descricao;

    /**
     * Associa a operação à descrição exibida na documentação.
     *
     * @param descricao texto curto da operação
     */
    Operacao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Retorna a descrição funcional da operação.
     *
     * @return texto em português
     */
    public String getDescricao() {
        return descricao;
    }
}
