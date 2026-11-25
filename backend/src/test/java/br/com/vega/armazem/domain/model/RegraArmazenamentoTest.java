package br.com.vega.armazem.domain.model;

import br.com.vega.armazem.domain.exception.RegraNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Cobre capacidade, mistura de tipos e a carência dos ingredientes secos.
 */
class RegraArmazenamentoTest {

    private final LocalDate hoje = LocalDate.of(2026, 11, 24);

    /**
     * Confirma os apelidos aceitos para o tipo seco.
     */
    @Test
    @DisplayName("aceita SECOS como apelido de SECO")
    void deveAceitarApelidoDosSecos() {
        assertEquals(TipoIngrediente.SECO, TipoIngrediente.interpretar("SECOS"));
        assertEquals(TipoIngrediente.LIQUIDO, TipoIngrediente.interpretar("Líquidos"));
        assertEquals(TipoIngrediente.REFRIGERADO, TipoIngrediente.interpretar("REFRIGERADOS"));
    }

    /**
     * Recusa uma entrada que ultrapassa os 600 kg de secos.
     */
    @Test
    @DisplayName("recusa entrada acima da capacidade")
    void deveRecusarCapacidadeEstourada() {
        Compartimento compartimento = vazio();
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> RegraArmazenamento.validarEntrada(compartimento, TipoIngrediente.SECO, new BigDecimal("600.001"), hoje));
        assertTrue(erro.getMessage().contains("não tem espaço suficiente"));
    }

    /**
     * Impede misturar líquido em um compartimento que ainda guarda seco.
     */
    @Test
    @DisplayName("recusa mistura de tipos com estoque")
    void deveRecusarMistura() {
        Compartimento compartimento = new Compartimento(1L, "C1", TipoIngrediente.SECO, new BigDecimal("10.000"), hoje);
        assertFalse(RegraArmazenamento.aceitaTipo(compartimento, TipoIngrediente.LIQUIDO, hoje));
    }

    /**
     * Depois de esvaziar um seco no mesmo dia, outro tipo só entra amanhã.
     */
    @Test
    @DisplayName("bloqueia outro tipo no dia em que houve seco")
    void deveBloquearOutroTipoNoMesmoDiaDoSeco() {
        Compartimento compartimento = new Compartimento(1L, "C1", null, new BigDecimal("0.000"), hoje);
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> RegraArmazenamento.validarEntrada(compartimento, TipoIngrediente.LIQUIDO, new BigDecimal("1"), hoje));
        assertTrue(erro.getMessage().contains("amanhã"));
        assertTrue(RegraArmazenamento.aceitaTipo(compartimento, TipoIngrediente.SECO, hoje));
    }

    /**
     * No dia seguinte o compartimento que guardou seco pode receber outro tipo.
     */
    @Test
    @DisplayName("libera outro tipo no dia seguinte")
    void deveLiberarOutroTipoNoDiaSeguinte() {
        Compartimento compartimento = new Compartimento(1L, "C1", null, new BigDecimal("0.000"), hoje.minusDays(1));
        assertTrue(RegraArmazenamento.aceitaTipo(compartimento, TipoIngrediente.REFRIGERADO, hoje));
        assertEquals(new BigDecimal("400.000"),
                RegraArmazenamento.calcularEspacoLivre(compartimento, TipoIngrediente.REFRIGERADO, hoje));
    }

    /**
     * Calcula a sobra descontando apenas o saldo do mesmo tipo.
     */
    @Test
    @DisplayName("calcula o espaço livre restante")
    void deveCalcularEspacoLivre() {
        Compartimento compartimento = new Compartimento(2L, "C2", TipoIngrediente.LIQUIDO, new BigDecimal("200.000"), null);
        assertEquals(new BigDecimal("300.000"),
                RegraArmazenamento.calcularEspacoLivre(compartimento, TipoIngrediente.LIQUIDO, hoje));
        assertEquals(new BigDecimal("0.000"),
                RegraArmazenamento.calcularEspacoLivre(compartimento, TipoIngrediente.SECO, hoje));
    }

    /**
     * Recusa saída maior que o saldo do ingrediente.
     */
    @Test
    @DisplayName("recusa saída acima do saldo")
    void deveRecusarSaidaMaiorQueOSaldo() {
        Compartimento compartimento = new Compartimento(1L, "C1", TipoIngrediente.SECO, new BigDecimal("10.000"), hoje);
        Ingrediente ingrediente = new Ingrediente(7L, "Farinha", TipoIngrediente.SECO, new BigDecimal("10.000"), 1L);
        assertThrows(RegraNegocioException.class,
                () -> RegraArmazenamento.validarSaida(compartimento, ingrediente, new BigDecimal("11")));
    }

    /**
     * Cria um compartimento vazio que nunca recebeu seco.
     *
     * @return compartimento C1 sem estoque
     */
    private Compartimento vazio() {
        return new Compartimento(1L, "C1", null, new BigDecimal("0.000"), null);
    }
}
