package br.com.vega.armazem.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Credenciais enviadas pela tela de login.
 *
 * @param usuario nome de login
 * @param senha senha em texto, conferida com o hash armazenado
 */
public record LoginRequest(
        @NotBlank(message = "O usuário é obrigatório.") String usuario,
        @NotBlank(message = "A senha é obrigatória.") String senha
) {
}
