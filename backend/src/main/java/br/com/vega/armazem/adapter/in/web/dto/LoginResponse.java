package br.com.vega.armazem.adapter.in.web.dto;

import java.util.List;

/**
 * Token devolvido após a autenticação bem-sucedida.
 *
 * @param token JWT compacto
 * @param tipo esquema de autorização, sempre Bearer
 * @param expiraEmMinutos validade informada ao cliente
 * @param usuario nome autenticado
 * @param papeis autoridades concedidas
 */
public record LoginResponse(String token, String tipo, long expiraEmMinutos, String usuario, List<String> papeis) {
}
