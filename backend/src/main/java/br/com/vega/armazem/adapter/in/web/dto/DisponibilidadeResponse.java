package br.com.vega.armazem.adapter.in.web.dto;

import java.math.BigDecimal;

/**
 * Compartimento com o espaço calculado para o tipo consultado.
 *
 * @param id identificador
 * @param codigo código C1 a C5
 * @param tipoArmazenado tipo que ocupa o compartimento
 * @param quantidadeAtual saldo
 * @param capacidadeMaxima capacidade do tipo consultado
 * @param espacoLivre espaço ainda utilizável
 * @param unidade unidade de medida
 */
public record DisponibilidadeResponse(
        Long id,
        String codigo,
        String tipoArmazenado,
        BigDecimal quantidadeAtual,
        BigDecimal capacidadeMaxima,
        BigDecimal espacoLivre,
        String unidade
) {
}
