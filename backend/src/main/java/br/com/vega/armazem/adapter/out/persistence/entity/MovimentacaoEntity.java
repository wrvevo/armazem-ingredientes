package br.com.vega.armazem.adapter.out.persistence.entity;

import br.com.vega.armazem.domain.model.Operacao;
import br.com.vega.armazem.domain.model.TipoIngrediente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Linha imutável do histórico de entrada ou saída.
 */
@Entity
@Table(name = "movimentacao", indexes = {
        @Index(name = "idx_movimentacao_data", columnList = "dataHora"),
        @Index(name = "idx_movimentacao_compartimento", columnList = "compartimentoId")
})
public class MovimentacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Operacao operacao;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal quantidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoIngrediente tipo;

    @Column(nullable = false, length = 80)
    private String responsavel;

    @Column(nullable = false)
    private Long compartimentoId;

    @Column(nullable = false, length = 2)
    private String compartimentoCodigo;

    @Column(nullable = false)
    private Long ingredienteId;

    @Column(nullable = false, length = 120)
    private String nomeIngrediente;

    /**
     * Cria a entidade vazia exigida pelo JPA.
     */
    public MovimentacaoEntity() {
    }

    /**
     * Retorna o identificador da movimentação.
     *
     * @return id gerado
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o identificador da movimentação.
     *
     * @param id id gerado
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna a data e hora da movimentação.
     *
     * @return instante do lançamento
     */
    public LocalDateTime getDataHora() {
        return dataHora;
    }

    /**
     * Define a data e hora da movimentação.
     *
     * @param dataHora instante do lançamento
     */
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    /**
     * Retorna a operação registrada.
     *
     * @return ENTRADA ou SAIDA
     */
    public Operacao getOperacao() {
        return operacao;
    }

    /**
     * Define a operação registrada.
     *
     * @param operacao ENTRADA ou SAIDA
     */
    public void setOperacao(Operacao operacao) {
        this.operacao = operacao;
    }

    /**
     * Retorna a quantidade movimentada.
     *
     * @return quantidade do lançamento
     */
    public BigDecimal getQuantidade() {
        return quantidade;
    }

    /**
     * Define a quantidade movimentada.
     *
     * @param quantidade quantidade do lançamento
     */
    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    /**
     * Retorna o tipo do ingrediente.
     *
     * @return classificação
     */
    public TipoIngrediente getTipo() {
        return tipo;
    }

    /**
     * Define o tipo do ingrediente.
     *
     * @param tipo classificação
     */
    public void setTipo(TipoIngrediente tipo) {
        this.tipo = tipo;
    }

    /**
     * Retorna o responsável pela movimentação.
     *
     * @return nome do responsável
     */
    public String getResponsavel() {
        return responsavel;
    }

    /**
     * Define o responsável pela movimentação.
     *
     * @param responsavel nome do responsável
     */
    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
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
     * Define o id do compartimento.
     *
     * @param compartimentoId id de 1 a 5
     */
    public void setCompartimentoId(Long compartimentoId) {
        this.compartimentoId = compartimentoId;
    }

    /**
     * Retorna o código do compartimento.
     *
     * @return C1 a C5
     */
    public String getCompartimentoCodigo() {
        return compartimentoCodigo;
    }

    /**
     * Define o código do compartimento.
     *
     * @param compartimentoCodigo C1 a C5
     */
    public void setCompartimentoCodigo(String compartimentoCodigo) {
        this.compartimentoCodigo = compartimentoCodigo;
    }

    /**
     * Retorna o id do ingrediente.
     *
     * @return identificador do ingrediente
     */
    public Long getIngredienteId() {
        return ingredienteId;
    }

    /**
     * Define o id do ingrediente.
     *
     * @param ingredienteId identificador do ingrediente
     */
    public void setIngredienteId(Long ingredienteId) {
        this.ingredienteId = ingredienteId;
    }

    /**
     * Retorna o nome do ingrediente.
     *
     * @return nome comercial
     */
    public String getNomeIngrediente() {
        return nomeIngrediente;
    }

    /**
     * Define o nome do ingrediente.
     *
     * @param nomeIngrediente nome comercial
     */
    public void setNomeIngrediente(String nomeIngrediente) {
        this.nomeIngrediente = nomeIngrediente;
    }
}
