package br.com.vega.armazem.domain.model;

import java.math.BigDecimal;

/**
 * Ingrediente enriquecido com o código do compartimento para a resposta da API.
 */
public final class IngredienteConsulta {

    private final Long id;
    private final String nome;
    private final TipoIngrediente tipo;
    private final BigDecimal quantidade;
    private final Long compartimentoId;
    private final String compartimentoCodigo;

    /**
     * Monta a consulta pronta para o adaptador web.
     *
     * @param id identificador do ingrediente
     * @param nome nome comercial
     * @param tipo classificação
     * @param quantidade saldo atual
     * @param compartimentoId id do compartimento
     * @param compartimentoCodigo código C1 a C5
     */
    public IngredienteConsulta(Long id, String nome, TipoIngrediente tipo, BigDecimal quantidade,
                               Long compartimentoId, String compartimentoCodigo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.compartimentoId = compartimentoId;
        this.compartimentoCodigo = compartimentoCodigo;
    }

    /**
     * Cria a consulta a partir do ingrediente e do compartimento em que ele está.
     *
     * @param ingrediente saldo persistido
     * @param compartimento compartimento correspondente
     * @return visão de consulta
     */
    public static IngredienteConsulta de(Ingrediente ingrediente, Compartimento compartimento) {
        return new IngredienteConsulta(ingrediente.getId(), ingrediente.getNome(), ingrediente.getTipo(),
                ingrediente.getQuantidade(), compartimento.getId(), compartimento.getCodigo());
    }

    /**
     * Retorna o identificador do ingrediente.
     *
     * @return id persistido
     */
    public Long getId() {
        return id;
    }

    /**
     * Retorna o nome do ingrediente.
     *
     * @return nome comercial
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna o tipo do ingrediente.
     *
     * @return classificação de armazenamento
     */
    public TipoIngrediente getTipo() {
        return tipo;
    }

    /**
     * Retorna o saldo atual.
     *
     * @return quantidade com três casas decimais
     */
    public BigDecimal getQuantidade() {
        return quantidade;
    }

    /**
     * Retorna o id do compartimento.
     *
     * @return id de 1 a 5
     */
    public Long getCompartimentoId() {
        return compartimentoId;
    }

    /**
     * Retorna o código do compartimento.
     *
     * @return C1 a C5
     */
    public String getCompartimentoCodigo() {
        return compartimentoCodigo;
    }
}
