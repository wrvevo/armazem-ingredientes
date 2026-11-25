package br.com.vega.armazem.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Corpo da retirada de um ingrediente já cadastrado.
 *
 * @param quantidade quantidade que sai
 * @param responsavel pessoa responsável pela saída
 */
public record SaidaRequest(
        @NotNull(message = "A quantidade é obrigatória.")
        @DecimalMin(value = "0.001", message = "A quantidade deve ser maior que zero.")
        @Digits(integer = 12, fraction = 3, message = "A quantidade aceita no máximo três casas decimais.")
        BigDecimal quantidade,
        @NotBlank(message = "O responsável é obrigatório.") String responsavel
) {
}
