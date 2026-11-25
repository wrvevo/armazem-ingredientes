package br.com.vega.armazem.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Ingrediente cadastrado e a quantidade que ainda permanece no compartimento.
 */
public final class Ingrediente {

    private final Long id;
    private final String nome;
    private final TipoIngrediente tipo;
    private final BigDecimal quantidade;
    private final Long compartimentoId;

    /**
     * Reconstrói o ingrediente a partir do estado persistido ou de um cadastro novo.
     *
     * @param id identificador gerado, nulo antes da persistência
     * @param nome nome comercial do ingrediente
     * @param tipo classificação de armazenamento
     * @param quantidade saldo atual
     * @param compartimentoId compartimento onde o saldo está guardado
     */
    public Ingrediente(Long id, String nome, TipoIngrediente tipo, BigDecimal quantidade, Long compartimentoId) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.quantidade = quantidade == null
                ? new BigDecimal("0.000")
                : quantidade.setScale(3, RoundingMode.HALF_UP);
        this.compartimentoId = compartimentoId;
    }

    /**
     * Reduz o saldo do ingrediente e devolve a nova versão imutável.
     *
     * @param quantidadeRetirada quantidade positiva da saída
     * @return ingrediente com o saldo diminuído
     */
    public Ingrediente retirar(BigDecimal quantidadeRetirada) {
        BigDecimal restante = quantidade.subtract(quantidadeRetirada).setScale(3, RoundingMode.HALF_UP);
        return new Ingrediente(id, nome, tipo, restante, compartimentoId);
    }

    /**
     * Retorna o identificador do ingrediente.
     *
     * @return id persistido ou nulo
     */
    public Long getId() {
        return id;
    }

    /**
     * Retorna o nome do ingrediente.
     *
     * @return nome informado no cadastro
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna o tipo de armazenamento.
     *
     * @return SECO, LIQUIDO ou REFRIGERADO
     */
    public TipoIngrediente getTipo() {
        return tipo;
    }

    /**
     * Retorna o saldo ainda armazenado.
     *
     * @return quantidade com três casas decimais
     */
    public BigDecimal getQuantidade() {
        return quantidade;
    }

    /**
     * Retorna o compartimento em que o ingrediente está.
     *
     * @return id do compartimento
     */
    public Long getCompartimentoId() {
        return compartimentoId;
    }
}
