package br.com.vega.armazem.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Corpo do cadastro de ingrediente, que também registra a entrada no estoque.
 *
 * @param nome nome comercial
 * @param tipo SECO, LIQUIDO, REFRIGERADO ou os plurais do enunciado
 * @param quantidade quantidade que entra
 * @param compartimentoId compartimento de 1 a 5
 * @param responsavel pessoa responsável pela entrada
 */
public record CadastroIngredienteRequest(
        @NotBlank(message = "O nome é obrigatório.") String nome,
        @NotBlank(message = "O tipo é obrigatório.") String tipo,
        @NotNull(message = "A quantidade é obrigatória.")
        @DecimalMin(value = "0.001", message = "A quantidade deve ser maior que zero.")
        @Digits(integer = 12, fraction = 3, message = "A quantidade aceita no máximo três casas decimais.")
        BigDecimal quantidade,
        @NotNull(message = "O compartimento é obrigatório.")
        @Min(value = 1, message = "O compartimento deve estar entre 1 e 5.")
        @Max(value = 5, message = "O compartimento deve estar entre 1 e 5.")
        Long compartimentoId,
        @NotBlank(message = "O responsável é obrigatório.") String responsavel
) {
}
