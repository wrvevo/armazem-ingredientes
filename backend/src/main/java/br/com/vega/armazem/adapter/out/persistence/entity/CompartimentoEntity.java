package br.com.vega.armazem.adapter.out.persistence.entity;

import br.com.vega.armazem.domain.model.TipoIngrediente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Linha persistida de um compartimento do armazém.
 */
@Entity
@Table(name = "compartimento")
public class CompartimentoEntity {

    @Id
    private Long id;

    @Column(nullable = false, unique = true, length = 2)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoIngrediente tipoAtual;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal quantidadeAtual;

    private LocalDate dataUltimoArmazenamentoSeco;

    /**
     * Cria a entidade vazia exigida pelo JPA.
     */
    public CompartimentoEntity() {
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
     * Define o identificador fixo do compartimento.
     *
     * @param id id de 1 a 5
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna o código visível.
     *
     * @return C1 a C5
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Define o código visível.
     *
     * @param codigo C1 a C5
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Retorna o tipo atualmente armazenado.
     *
     * @return tipo ou nulo
     */
    public TipoIngrediente getTipoAtual() {
        return tipoAtual;
    }

    /**
     * Define o tipo atualmente armazenado.
     *
     * @param tipoAtual tipo ou nulo quando vazio
     */
    public void setTipoAtual(TipoIngrediente tipoAtual) {
        this.tipoAtual = tipoAtual;
    }

    /**
     * Retorna a quantidade armazenada.
     *
     * @return saldo do compartimento
     */
    public BigDecimal getQuantidadeAtual() {
        return quantidadeAtual;
    }

    /**
     * Define a quantidade armazenada.
     *
     * @param quantidadeAtual saldo do compartimento
     */
    public void setQuantidadeAtual(BigDecimal quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    /**
     * Retorna a data da última entrada de seco.
     *
     * @return data ou nulo
     */
    public LocalDate getDataUltimoArmazenamentoSeco() {
        return dataUltimoArmazenamentoSeco;
    }

    /**
     * Define a data da última entrada de seco.
     *
     * @param dataUltimoArmazenamentoSeco data ou nulo
     */
    public void setDataUltimoArmazenamentoSeco(LocalDate dataUltimoArmazenamentoSeco) {
        this.dataUltimoArmazenamentoSeco = dataUltimoArmazenamentoSeco;
    }
}
