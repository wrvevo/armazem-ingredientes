package br.com.vega.armazem.adapter.in.web.dto;

import java.time.LocalDateTime;

/**
 * Erro padronizado devolvido pela API.
 *
 * @param timestamp instante do erro
 * @param status código HTTP
 * @param erro título curto
 * @param mensagem explicação para o cliente
 * @param caminho caminho da requisição
 */
public record ErroResponse(LocalDateTime timestamp, int status, String erro, String mensagem, String caminho) {
}
