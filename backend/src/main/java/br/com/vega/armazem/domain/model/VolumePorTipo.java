package br.com.vega.armazem.domain.model;

import java.math.BigDecimal;

/**
 * Total armazenado de um tipo, somando todos os compartimentos.
 */
public final class VolumePorTipo {

    private final TipoIngrediente tipo;
    private final BigDecimal quantidadeTotal;

    /**
     * Cria o total consolidado de um tipo.
     *
     * @param tipo classificação somada
     * @param quantidadeTotal soma do saldo, inclusive zero
     */
    public VolumePorTipo(TipoIngrediente tipo, BigDecimal quantidadeTotal) {
        this.tipo = tipo;
        this.quantidadeTotal = quantidadeTotal;
    }

    /**
     * Retorna o tipo consolidado.
     *
     * @return SECO, LIQUIDO ou REFRIGERADO
     */
    public TipoIngrediente getTipo() {
        return tipo;
    }

    /**
     * Retorna a quantidade total daquele tipo.
     *
     * @return soma com três casas decimais
     */
    public BigDecimal getQuantidadeTotal() {
        return quantidadeTotal;
    }
}
