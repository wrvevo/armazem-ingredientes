package br.com.vega.armazem.adapter.out.persistence.entity;

import br.com.vega.armazem.domain.model.TipoIngrediente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Linha persistida do saldo de um ingrediente.
 */
@Entity
@Table(name = "ingrediente")
public class IngredienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoIngrediente tipo;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal quantidade;

    @Column(nullable = false)
    private Long compartimentoId;

    /**
     * Cria a entidade vazia exigida pelo JPA.
     */
    public IngredienteEntity() {
    }

    /**
     * Retorna o identificador do ingrediente.
     *
     * @return id gerado
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o identificador do ingrediente.
     *
     * @param id id gerado
     */
    public void setId(Long id) {
        this.id = id;
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
     * Define o nome do ingrediente.
     *
     * @param nome nome comercial
     */
    public void setNome(String nome) {
        this.nome = nome;
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
     * Retorna o saldo do ingrediente.
     *
     * @return quantidade atual
     */
    public BigDecimal getQuantidade() {
        return quantidade;
    }

    /**
     * Define o saldo do ingrediente.
     *
     * @param quantidade quantidade atual
     */
    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    /**
     * Retorna o compartimento do ingrediente.
     *
     * @return id do compartimento
     */
    public Long getCompartimentoId() {
        return compartimentoId;
    }

    /**
     * Define o compartimento do ingrediente.
     *
     * @param compartimentoId id do compartimento
     */
    public void setCompartimentoId(Long compartimentoId) {
        this.compartimentoId = compartimentoId;
    }
}
