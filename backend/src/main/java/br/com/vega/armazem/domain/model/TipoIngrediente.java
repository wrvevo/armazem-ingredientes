package br.com.vega.armazem.domain.model;

import br.com.vega.armazem.domain.exception.RegraNegocioException;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Locale;

/**
 * Tipos de ingrediente aceitos pelo armazém e a capacidade máxima de cada um.
 */
public enum TipoIngrediente {

    SECO(new BigDecimal("600.000"), "kg"),
    LIQUIDO(new BigDecimal("500.000"), "L"),
    REFRIGERADO(new BigDecimal("400.000"), "kg");

    private final BigDecimal capacidadeMaxima;
    private final String unidade;

    /**
     * Associa o tipo à capacidade máxima e à unidade de medida.
     *
     * @param capacidadeMaxima limite do compartimento para este tipo
     * @param unidade símbolo exibido nas mensagens de capacidade
     */
    TipoIngrediente(BigDecimal capacidadeMaxima, String unidade) {
        this.capacidadeMaxima = capacidadeMaxima;
        this.unidade = unidade;
    }

    /**
     * Retorna a capacidade máxima permitida em um compartimento deste tipo.
     *
     * @return capacidade com três casas decimais
     */
    public BigDecimal getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    /**
     * Retorna a unidade de medida usada por este tipo.
     *
     * @return "kg" ou "L"
     */
    public String getUnidade() {
        return unidade;
    }

    /**
     * Converte o texto recebido na API para o tipo canônico.
     * Aceita os nomes do enunciado (SECOS, LIQUIDOS, REFRIGERADOS) e a forma singular sem acento.
     *
     * @param valor texto informado pelo cliente
     * @return tipo reconhecido
     * @throws RegraNegocioException quando o texto não corresponde a um tipo válido
     */
    public static TipoIngrediente interpretar(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new RegraNegocioException("O tipo do ingrediente é obrigatório.");
        }
        String normalizado = Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);
        return switch (normalizado) {
            case "SECO", "SECOS" -> SECO;
            case "LIQUIDO", "LIQUIDOS" -> LIQUIDO;
            case "REFRIGERADO", "REFRIGERADOS" -> REFRIGERADO;
            default -> throw new RegraNegocioException(
                    "Tipo de ingrediente inválido: " + valor + ". Use SECO, LIQUIDO ou REFRIGERADO.");
        };
    }
}
