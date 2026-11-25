package br.com.vega.armazem.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Lançamento imutável de entrada ou saída. Nenhuma movimentação existe sem este registro.
 */
public final class Movimentacao {

    private final Long id;
    private final LocalDateTime dataHora;
    private final Operacao operacao;
    private final BigDecimal quantidade;
    private final TipoIngrediente tipo;
    private final String responsavel;
    private final Long compartimentoId;
    private final String compartimentoCodigo;
    private final Long ingredienteId;
    private final String nomeIngrediente;

    /**
     * Reconstrói uma movimentação já ocorrida.
     *
     * @param id identificador gerado
     * @param dataHora instante da operação
     * @param operacao ENTRADA ou SAIDA
     * @param quantidade quantidade movimentada
     * @param tipo tipo do ingrediente
     * @param responsavel pessoa que executou a operação
     * @param compartimentoId id do compartimento
     * @param compartimentoCodigo código C1 a C5
     * @param ingredienteId id do ingrediente
     * @param nomeIngrediente nome do ingrediente
     */
    public Movimentacao(Long id, LocalDateTime dataHora, Operacao operacao, BigDecimal quantidade,
                        TipoIngrediente tipo, String responsavel, Long compartimentoId, String compartimentoCodigo,
                        Long ingredienteId, String nomeIngrediente) {
        this.id = id;
        this.dataHora = dataHora;
        this.operacao = operacao;
        this.quantidade = quantidade.setScale(3, RoundingMode.HALF_UP);
        this.tipo = tipo;
        this.responsavel = responsavel;
        this.compartimentoId = compartimentoId;
        this.compartimentoCodigo = compartimentoCodigo;
        this.ingredienteId = ingredienteId;
        this.nomeIngrediente = nomeIngrediente;
    }

    /**
     * Cria o lançamento de entrada que será persistido na mesma transação do estoque.
     *
     * @param dataHora instante da entrada
     * @param ingrediente ingrediente já persistido
     * @param quantidade quantidade que entrou
     * @param responsavel responsável informado
     * @param compartimento compartimento de destino
     * @return movimentação pronta para gravação
     */
    public static Movimentacao entrada(LocalDateTime dataHora, Ingrediente ingrediente, BigDecimal quantidade,
                                       String responsavel, Compartimento compartimento) {
        return new Movimentacao(null, dataHora, Operacao.ENTRADA, quantidade, ingrediente.getTipo(), responsavel,
                compartimento.getId(), compartimento.getCodigo(), ingrediente.getId(), ingrediente.getNome());
    }

    /**
     * Cria o lançamento de saída que será persistido na mesma transação do estoque.
     *
     * @param dataHora instante da saída
     * @param ingrediente ingrediente depois da baixa
     * @param quantidade quantidade que saiu
     * @param responsavel responsável informado
     * @param compartimento compartimento de origem
     * @return movimentação pronta para gravação
     */
    public static Movimentacao saida(LocalDateTime dataHora, Ingrediente ingrediente, BigDecimal quantidade,
                                     String responsavel, Compartimento compartimento) {
        return new Movimentacao(null, dataHora, Operacao.SAIDA, quantidade, ingrediente.getTipo(), responsavel,
                compartimento.getId(), compartimento.getCodigo(), ingrediente.getId(), ingrediente.getNome());
    }

    /**
     * Retorna o identificador da movimentação.
     *
     * @return id persistido ou nulo
     */
    public Long getId() {
        return id;
    }

    /**
     * Retorna o instante da movimentação.
     *
     * @return data e hora locais
     */
    public LocalDateTime getDataHora() {
        return dataHora;
    }

    /**
     * Retorna o tipo da operação.
     *
     * @return ENTRADA ou SAIDA
     */
    public Operacao getOperacao() {
        return operacao;
    }

    /**
     * Retorna a quantidade movimentada.
     *
     * @return quantidade com três casas decimais
     */
    public BigDecimal getQuantidade() {
        return quantidade;
    }

    /**
     * Retorna o tipo do ingrediente movimentado.
     *
     * @return classificação de armazenamento
     */
    public TipoIngrediente getTipo() {
        return tipo;
    }

    /**
     * Retorna quem executou a movimentação.
     *
     * @return nome do responsável
     */
    public String getResponsavel() {
        return responsavel;
    }

    /**
     * Retorna o id do compartimento envolvido.
     *
     * @return id de 1 a 5
     */
    public Long getCompartimentoId() {
        return compartimentoId;
    }

    /**
     * Retorna o código do compartimento envolvido.
     *
     * @return C1 a C5
     */
    public String getCompartimentoCodigo() {
        return compartimentoCodigo;
    }

    /**
     * Retorna o id do ingrediente movimentado.
     *
     * @return identificador do ingrediente
     */
    public Long getIngredienteId() {
        return ingredienteId;
    }

    /**
     * Retorna o nome do ingrediente movimentado.
     *
     * @return nome comercial
     */
    public String getNomeIngrediente() {
        return nomeIngrediente;
    }
}
