package br.com.vega.armazem.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Lançamento do histórico de entrada ou saída.
 *
 * @param id identificador
 * @param dataHora instante da operação
 * @param operacao ENTRADA ou SAIDA
 * @param quantidade quantidade movimentada
 * @param tipo tipo do ingrediente
 * @param responsavel pessoa responsável
 * @param compartimentoId id do compartimento
 * @param compartimentoCodigo código do compartimento
 * @param ingredienteId id do ingrediente
 * @param nomeIngrediente nome do ingrediente
 */
public record MovimentacaoResponse(
        Long id,
        LocalDateTime dataHora,
        String operacao,
        BigDecimal quantidade,
        String tipo,
        String responsavel,
        Long compartimentoId,
        String compartimentoCodigo,
        Long ingredienteId,
        String nomeIngrediente
) {
}
