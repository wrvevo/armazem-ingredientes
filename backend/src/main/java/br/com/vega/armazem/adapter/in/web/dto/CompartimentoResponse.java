package br.com.vega.armazem.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Estado atual de um compartimento.
 *
 * @param id identificador
 * @param codigo código C1 a C5
 * @param tipoAtual tipo ocupante, ou nulo
 * @param quantidadeAtual saldo
 * @param capacidadeMaxima capacidade do tipo atual, ou nula quando vazio
 * @param espacoLivre espaço restante do tipo atual, ou nulo quando vazio
 * @param unidade unidade do tipo atual
 * @param dataUltimoArmazenamentoSeco último dia em que recebeu secos
 */
public record CompartimentoResponse(
        Long id,
        String codigo,
        String tipoAtual,
        BigDecimal quantidadeAtual,
        BigDecimal capacidadeMaxima,
        BigDecimal espacoLivre,
        String unidade,
        LocalDate dataUltimoArmazenamentoSeco
) {
}
