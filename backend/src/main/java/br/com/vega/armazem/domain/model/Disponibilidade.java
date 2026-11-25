package br.com.vega.armazem.domain.model;

import java.math.BigDecimal;

/**
 * Visão de um compartimento com o espaço livre calculado para um tipo pedido.
 */
public final class Disponibilidade {

    private final Long id;
    private final String codigo;
    private final TipoIngrediente tipoArmazenado;
    private final BigDecimal quantidadeAtual;
    private final BigDecimal capacidadeMaxima;
    private final BigDecimal espacoLivre;
    private final String unidade;

    /**
     * Monta o resultado da consulta de espaço.
     *
     * @param id identificador do compartimento
     * @param codigo código C1 a C5
     * @param tipoArmazenado tipo atualmente guardado, ou nulo
     * @param quantidadeAtual saldo atual
     * @param capacidadeMaxima capacidade do tipo consultado ou do tipo ocupante
     * @param espacoLivre espaço ainda utilizável
     * @param unidade unidade da capacidade
     */
    public Disponibilidade(Long id, String codigo, TipoIngrediente tipoArmazenado, BigDecimal quantidadeAtual,
                           BigDecimal capacidadeMaxima, BigDecimal espacoLivre, String unidade) {
        this.id = id;
        this.codigo = codigo;
        this.tipoArmazenado = tipoArmazenado;
        this.quantidadeAtual = quantidadeAtual;
        this.capacidadeMaxima = capacidadeMaxima;
        this.espacoLivre = espacoLivre;
        this.unidade = unidade;
    }

    /**
     * Retorna o identificador do compartimento.
     *
     * @return id de 1 a 5
     */
    public Long getId() {
        return id;
    }

    /**
     * Retorna o código do compartimento.
     *
     * @return C1 a C5
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Retorna o tipo que ocupa o compartimento.
     *
     * @return tipo atual ou nulo
     */
    public TipoIngrediente getTipoArmazenado() {
        return tipoArmazenado;
    }

    /**
     * Retorna o saldo atual.
     *
     * @return quantidade armazenada
     */
    public BigDecimal getQuantidadeAtual() {
        return quantidadeAtual;
    }

    /**
     * Retorna a capacidade considerada no cálculo.
     *
     * @return capacidade máxima
     */
    public BigDecimal getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    /**
     * Retorna o espaço ainda livre.
     *
     * @return quantidade que ainda cabe
     */
    public BigDecimal getEspacoLivre() {
        return espacoLivre;
    }

    /**
     * Retorna a unidade de medida.
     *
     * @return kg ou L
     */
    public String getUnidade() {
        return unidade;
    }
}
