package br.com.vega.armazem.adapter.in.web.dto;

import java.math.BigDecimal;

/**
 * Total armazenado de um tipo, no formato pedido pelo desafio.
 *
 * @param type nome canônico do tipo
 * @param totalQuantity soma do saldo
 */
public record VolumeResponse(String type, BigDecimal totalQuantity) {
}
