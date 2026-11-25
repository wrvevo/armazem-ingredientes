package br.com.vega.armazem.domain.model;

import br.com.vega.armazem.domain.exception.RegraNegocioException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Compartimento físico do armazém. Existem cinco instâncias fixas: C1 a C5.
 */
public final class Compartimento {

    private final Long id;
    private final String codigo;
    private final TipoIngrediente tipoAtual;
    private final BigDecimal quantidadeAtual;
    private final LocalDate dataUltimoArmazenamentoSeco;

    /**
     * Reconstrói o compartimento a partir do estado persistido.
     *
     * @param id identificador de 1 a 5
     * @param codigo código visível, de C1 a C5
     * @param tipoAtual tipo ocupando o compartimento, ou nulo quando vazio
     * @param quantidadeAtual soma armazenada, com três casas decimais
     * @param dataUltimoArmazenamentoSeco dia da última entrada de seco
     */
    public Compartimento(Long id, String codigo, TipoIngrediente tipoAtual, BigDecimal quantidadeAtual,
                         LocalDate dataUltimoArmazenamentoSeco) {
        this.id = id;
        this.codigo = codigo;
        this.tipoAtual = tipoAtual;
        this.quantidadeAtual = quantidadeAtual == null
                ? new BigDecimal("0.000")
                : quantidadeAtual.setScale(3, RoundingMode.HALF_UP);
        this.dataUltimoArmazenamentoSeco = dataUltimoArmazenamentoSeco;
    }

    /**
     * Indica se ainda há volume armazenado.
     *
     * @return verdadeiro quando a quantidade é maior que zero
     */
    public boolean possuiEstoque() {
        return quantidadeAtual.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Aplica uma entrada já validada e devolve o novo estado do compartimento.
     *
     * @param tipo tipo que será armazenado
     * @param quantidade quantidade positiva da entrada
     * @param hoje data corrente usada na regra dos secos
     * @return compartimento com estoque e tipo atualizados
     */
    public Compartimento registrarEntrada(TipoIngrediente tipo, BigDecimal quantidade, LocalDate hoje) {
        RegraArmazenamento.validarEntrada(this, tipo, quantidade, hoje);
        BigDecimal novaQuantidade = quantidadeAtual.add(quantidade).setScale(3, RoundingMode.HALF_UP);
        LocalDate dataSeco = tipo == TipoIngrediente.SECO ? hoje : dataUltimoArmazenamentoSeco;
        return new Compartimento(id, codigo, tipo, novaQuantidade, dataSeco);
    }

    /**
     * Reduz o estoque total do compartimento depois que a saída do ingrediente foi validada.
     *
     * @param quantidade quantidade positiva que sai
     * @return compartimento atualizado; o tipo é limpo quando o estoque chega a zero
     */
    public Compartimento registrarSaida(BigDecimal quantidade) {
        BigDecimal restante = quantidadeAtual.subtract(quantidade).setScale(3, RoundingMode.HALF_UP);
        if (restante.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("O compartimento " + codigo + " não possui quantidade suficiente para a saída.");
        }
        if (restante.compareTo(BigDecimal.ZERO) == 0) {
            return new Compartimento(id, codigo, null, new BigDecimal("0.000"), dataUltimoArmazenamentoSeco);
        }
        return new Compartimento(id, codigo, tipoAtual, restante, dataUltimoArmazenamentoSeco);
    }

    /**
     * Retorna o identificador numérico do compartimento.
     *
     * @return id de 1 a 5
     */
    public Long getId() {
        return id;
    }

    /**
     * Retorna o código visível do compartimento.
     *
     * @return C1, C2, C3, C4 ou C5
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Retorna o tipo atualmente armazenado.
     *
     * @return tipo ocupante ou nulo quando o compartimento está vazio
     */
    public TipoIngrediente getTipoAtual() {
        return tipoAtual;
    }

    /**
     * Retorna a quantidade total armazenada.
     *
     * @return quantidade com três casas decimais
     */
    public BigDecimal getQuantidadeAtual() {
        return quantidadeAtual;
    }

    /**
     * Retorna o dia em que o compartimento recebeu ingredientes secos pela última vez.
     *
     * @return data ou nulo se nunca recebeu secos
     */
    public LocalDate getDataUltimoArmazenamentoSeco() {
        return dataUltimoArmazenamentoSeco;
    }
}
