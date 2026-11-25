package br.com.vega.armazem.application;

import br.com.vega.armazem.domain.exception.RegraNegocioException;
import br.com.vega.armazem.domain.model.Compartimento;
import br.com.vega.armazem.domain.model.Ingrediente;
import br.com.vega.armazem.domain.model.Movimentacao;
import br.com.vega.armazem.domain.model.Operacao;
import br.com.vega.armazem.domain.model.TipoIngrediente;
import br.com.vega.armazem.domain.port.out.CompartimentoPort;
import br.com.vega.armazem.domain.port.out.HistoricoPort;
import br.com.vega.armazem.domain.port.out.IngredientePort;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Garante que entrada e saída só concluem quando o histórico também é gravado.
 */
@ExtendWith(MockitoExtension.class)
class ServicosMovimentacaoTest {

    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");

    @Mock
    private CompartimentoPort compartimentoPort;

    @Mock
    private IngredientePort ingredientePort;

    @Mock
    private HistoricoPort historicoPort;

    private CadastrarIngredienteService cadastrar;
    private RegistrarSaidaService registrarSaida;

    /**
     * Monta os serviços com relógio fixo e métricas em memória.
     */
    @BeforeEach
    void preparar() {
        Clock relogio = Clock.fixed(LocalDate.of(2026, 11, 24).atStartOfDay(ZONA).toInstant(), ZONA);
        SimpleMeterRegistry metricas = new SimpleMeterRegistry();
        cadastrar = new CadastrarIngredienteService(compartimentoPort, ingredientePort, historicoPort, relogio, metricas);
        registrarSaida = new RegistrarSaidaService(compartimentoPort, ingredientePort, historicoPort, relogio, metricas);
    }

    /**
     * Confirma que a entrada persiste o ingrediente, o compartimento e o histórico.
     */
    @Test
    @DisplayName("grava histórico na entrada aceita")
    void deveGravarHistoricoNaEntrada() {
        Compartimento vazio = new Compartimento(1L, "C1", null, new BigDecimal("0.000"), null);
        when(compartimentoPort.buscarPorIdComBloqueio(1L)).thenReturn(Optional.of(vazio));
        when(compartimentoPort.salvar(any())).thenAnswer(invocacao -> invocacao.getArgument(0));
        when(ingredientePort.salvar(any())).thenAnswer(invocacao -> {
            Ingrediente ingrediente = invocacao.getArgument(0);
            return new Ingrediente(10L, ingrediente.getNome(), ingrediente.getTipo(), ingrediente.getQuantidade(),
                    ingrediente.getCompartimentoId());
        });
        when(historicoPort.registrar(any())).thenAnswer(invocacao -> invocacao.getArgument(0));

        cadastrar.cadastrar("Farinha de Trigo", TipoIngrediente.SECO, new BigDecimal("400"), 1L, "pedro");

        ArgumentCaptor<Movimentacao> historico = ArgumentCaptor.forClass(Movimentacao.class);
        verify(historicoPort).registrar(historico.capture());
        assertEquals(Operacao.ENTRADA, historico.getValue().getOperacao());
        assertEquals("pedro", historico.getValue().getResponsavel());
        assertEquals(new BigDecimal("400.000"), historico.getValue().getQuantidade());
    }

    /**
     * Garante que uma mistura inválida não gera histórico nem saldo.
     */
    @Test
    @DisplayName("não grava histórico quando a entrada é recusada")
    void naoDeveGravarHistoricoQuandoRecusado() {
        Compartimento ocupado = new Compartimento(1L, "C1", TipoIngrediente.SECO, new BigDecimal("100.000"),
                LocalDate.of(2026, 11, 24));
        when(compartimentoPort.buscarPorIdComBloqueio(1L)).thenReturn(Optional.of(ocupado));

        assertThrows(RegraNegocioException.class, () -> cadastrar.cadastrar(
                "Leite", TipoIngrediente.LIQUIDO, new BigDecimal("10"), 1L, "pedro"));

        verify(historicoPort, never()).registrar(any());
        verify(ingredientePort, never()).salvar(any());
    }

    /**
     * Confirma que a saída também produz um lançamento de histórico.
     */
    @Test
    @DisplayName("grava histórico na saída aceita")
    void deveGravarHistoricoNaSaida() {
        Ingrediente ingrediente = new Ingrediente(10L, "Farinha de Trigo", TipoIngrediente.SECO, new BigDecimal("400.000"), 1L);
        Compartimento compartimento = new Compartimento(1L, "C1", TipoIngrediente.SECO, new BigDecimal("400.000"),
                LocalDate.of(2026, 11, 24));
        when(ingredientePort.buscarPorId(10L)).thenReturn(Optional.of(ingrediente));
        when(ingredientePort.buscarPorIdComBloqueio(10L)).thenReturn(Optional.of(ingrediente));
        when(compartimentoPort.buscarPorIdComBloqueio(1L)).thenReturn(Optional.of(compartimento));
        when(ingredientePort.salvar(any())).thenAnswer(invocacao -> invocacao.getArgument(0));
        when(compartimentoPort.salvar(any())).thenAnswer(invocacao -> invocacao.getArgument(0));
        when(historicoPort.registrar(any())).thenAnswer(invocacao -> invocacao.getArgument(0));

        registrarSaida.registrar(10L, new BigDecimal("50"), "pedro");

        ArgumentCaptor<Movimentacao> historico = ArgumentCaptor.forClass(Movimentacao.class);
        verify(historicoPort).registrar(historico.capture());
        assertEquals(Operacao.SAIDA, historico.getValue().getOperacao());
        assertEquals(new BigDecimal("50.000"), historico.getValue().getQuantidade());
    }
}
