package br.com.vega.armazem.adapter.in.web.dto;

import java.math.BigDecimal;

/**
 * Saldo de um ingrediente devolvido pela API.
 *
 * @param id identificador
 * @param nome nome comercial
 * @param tipo classificação canônica
 * @param quantidade saldo atual
 * @param compartimentoId id do compartimento
 * @param compartimentoCodigo código C1 a C5
 */
public record IngredienteResponse(
        Long id,
        String nome,
        String tipo,
        BigDecimal quantidade,
        Long compartimentoId,
        String compartimentoCodigo
) {
}
