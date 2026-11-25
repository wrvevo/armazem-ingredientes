package br.com.vega.armazem.domain.model;

import br.com.vega.armazem.domain.exception.RegraNegocioException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Regras puras de capacidade, mistura de tipos e carência dos ingredientes secos.
 */
public final class RegraArmazenamento {

    /**
     * Impede a instanciação desta classe de funções.
     */
    private RegraArmazenamento() {
    }

    /**
     * Garante que a entrada respeita tipo, período e capacidade do compartimento.
     *
     * @param compartimento destino da entrada
     * @param tipo tipo do ingrediente
     * @param quantidade quantidade positiva solicitada
     * @param hoje data corrente no fuso America/Sao_Paulo
     * @throws RegraNegocioException quando a entrada não pode ser aceita
     */
    public static void validarEntrada(Compartimento compartimento, TipoIngrediente tipo,
                                      BigDecimal quantidade, LocalDate hoje) {
        exigirQuantidadePositiva(quantidade);
        if (tipo == null) {
            throw new RegraNegocioException("O tipo do ingrediente é obrigatório.");
        }
        if (!aceitaTipo(compartimento, tipo, hoje)) {
            if (compartimento.possuiEstoque()) {
                throw new RegraNegocioException("O compartimento " + compartimento.getCodigo()
                        + " já armazena " + compartimento.getTipoAtual()
                        + " neste período e não pode receber " + tipo + ".");
            }
            throw new RegraNegocioException("O compartimento " + compartimento.getCodigo()
                    + " armazenou ingredientes secos hoje e só pode armazenar outro tipo amanhã.");
        }
        BigDecimal livre = calcularEspacoLivre(compartimento, tipo, hoje);
        if (livre.compareTo(quantidade) < 0) {
            throw new RegraNegocioException("O compartimento " + compartimento.getCodigo()
                    + " não tem espaço suficiente para " + tipo
                    + ". Capacidade máxima: " + tipo.getCapacidadeMaxima() + " " + tipo.getUnidade()
                    + ". Ocupado: " + compartimento.getQuantidadeAtual()
                    + ". Solicitado: " + quantidade.setScale(3, RoundingMode.HALF_UP)
                    + ". Livre: " + livre + ".");
        }
    }

    /**
     * Garante que a saída não ultrapassa o estoque do ingrediente nem o do compartimento.
     *
     * @param compartimento compartimento de origem
     * @param ingrediente ingrediente que será retirado
     * @param quantidade quantidade positiva da saída
     * @throws RegraNegocioException quando a saída é inválida
     */
    public static void validarSaida(Compartimento compartimento, Ingrediente ingrediente, BigDecimal quantidade) {
        exigirQuantidadePositiva(quantidade);
        if (!compartimento.getId().equals(ingrediente.getCompartimentoId())) {
            throw new RegraNegocioException("O ingrediente não pertence ao compartimento informado.");
        }
        if (ingrediente.getQuantidade().compareTo(quantidade) < 0) {
            throw new RegraNegocioException("A quantidade da saída é maior que o estoque do ingrediente "
                    + ingrediente.getNome() + ". Disponível: " + ingrediente.getQuantidade() + ".");
        }
        if (!compartimento.possuiEstoque() || compartimento.getTipoAtual() != ingrediente.getTipo()) {
            throw new RegraNegocioException("O compartimento " + compartimento.getCodigo()
                    + " não possui estoque de " + ingrediente.getTipo() + " para esta saída.");
        }
    }

    /**
     * Informa se o compartimento pode receber o tipo na data corrente.
     *
     * @param compartimento compartimento analisado
     * @param tipo tipo desejado
     * @param hoje data corrente
     * @return verdadeiro quando não há mistura proibida nem bloqueio de seco no mesmo dia
     */
    public static boolean aceitaTipo(Compartimento compartimento, TipoIngrediente tipo, LocalDate hoje) {
        if (compartimento.possuiEstoque() && compartimento.getTipoAtual() != tipo) {
            return false;
        }
        return compartimento.possuiEstoque()
                || compartimento.getDataUltimoArmazenamentoSeco() == null
                || !compartimento.getDataUltimoArmazenamentoSeco().equals(hoje)
                || tipo == TipoIngrediente.SECO;
    }

    /**
     * Calcula o espaço ainda disponível para o tipo pedido.
     * Compartimentos incompatíveis devolvem zero, sem percorrer outras estruturas.
     *
     * @param compartimento compartimento analisado
     * @param tipo tipo que se pretende armazenar
     * @param hoje data corrente
     * @return espaço livre com três casas decimais
     */
    public static BigDecimal calcularEspacoLivre(Compartimento compartimento, TipoIngrediente tipo, LocalDate hoje) {
        if (!aceitaTipo(compartimento, tipo, hoje)) {
            return new BigDecimal("0.000");
        }
        BigDecimal ocupado = compartimento.possuiEstoque()
                ? compartimento.getQuantidadeAtual()
                : new BigDecimal("0.000");
        return tipo.getCapacidadeMaxima().subtract(ocupado).setScale(3, RoundingMode.HALF_UP);
    }

    /**
     * Rejeita quantidade nula ou não positiva.
     *
     * @param quantidade valor informado
     * @throws RegraNegocioException quando a quantidade não é maior que zero
     */
    private static void exigirQuantidadePositiva(BigDecimal quantidade) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("A quantidade deve ser maior que zero.");
        }
    }
}
