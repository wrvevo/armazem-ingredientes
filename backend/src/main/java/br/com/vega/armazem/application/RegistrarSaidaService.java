package br.com.vega.armazem.application;

import br.com.vega.armazem.domain.exception.RecursoNaoEncontradoException;
import br.com.vega.armazem.domain.exception.RegraNegocioException;
import br.com.vega.armazem.domain.model.Compartimento;
import br.com.vega.armazem.domain.model.Ingrediente;
import br.com.vega.armazem.domain.model.IngredienteConsulta;
import br.com.vega.armazem.domain.model.Movimentacao;
import br.com.vega.armazem.domain.model.RegraArmazenamento;
import br.com.vega.armazem.domain.port.in.RegistrarSaidaUseCase;
import br.com.vega.armazem.domain.port.out.CompartimentoPort;
import br.com.vega.armazem.domain.port.out.HistoricoPort;
import br.com.vega.armazem.domain.port.out.IngredientePort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Orquestra a saída de estoque bloqueando primeiro o compartimento e depois o ingrediente.
 */
@Service
public class RegistrarSaidaService implements RegistrarSaidaUseCase {

    private static final Logger LOGGER = LogManager.getLogger(RegistrarSaidaService.class);

    private final CompartimentoPort compartimentoPort;
    private final IngredientePort ingredientePort;
    private final HistoricoPort historicoPort;
    private final Clock relogio;
    private final Counter saidas;

    /**
     * Recebe as portas, o relógio e o registro de métricas da saída.
     *
     * @param compartimentoPort porta dos compartimentos
     * @param ingredientePort porta dos ingredientes
     * @param historicoPort porta do histórico
     * @param relogio relógio usado na data da movimentação
     * @param meterRegistry registro de métricas do Micrometer
     */
    public RegistrarSaidaService(CompartimentoPort compartimentoPort, IngredientePort ingredientePort,
                                 HistoricoPort historicoPort, Clock relogio, MeterRegistry meterRegistry) {
        this.compartimentoPort = compartimentoPort;
        this.ingredientePort = ingredientePort;
        this.historicoPort = historicoPort;
        this.relogio = relogio;
        this.saidas = meterRegistry.counter("armazem.movimentacoes", "operacao", "SAIDA");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public IngredienteConsulta registrar(Long ingredienteId, BigDecimal quantidade, String responsavel) {
        String responsavelLimpo = exigirTexto(responsavel, "O responsável pela movimentação é obrigatório.");
        Ingrediente referencia = ingredientePort.buscarPorId(ingredienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Ingrediente " + ingredienteId + " não foi encontrado."));
        Compartimento compartimento = compartimentoPort.buscarPorIdComBloqueio(referencia.getCompartimentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Compartimento " + referencia.getCompartimentoId() + " não foi encontrado."));
        Ingrediente ingrediente = ingredientePort.buscarPorIdComBloqueio(ingredienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Ingrediente " + ingredienteId + " não foi encontrado."));
        RegraArmazenamento.validarSaida(compartimento, ingrediente, quantidade);
        Ingrediente atualizado = ingredientePort.salvar(ingrediente.retirar(quantidade));
        Compartimento compartimentoAtualizado = compartimentoPort.salvar(compartimento.registrarSaida(quantidade));
        historicoPort.registrar(Movimentacao.saida(
                LocalDateTime.now(relogio), atualizado, quantidade, responsavelLimpo, compartimento));
        saidas.increment();
        LOGGER.info("Saída registrada: ingrediente={} quantidade={} compartimento={} responsavel={} saldo={}",
                atualizado.getNome(), quantidade, compartimento.getCodigo(), responsavelLimpo, atualizado.getQuantidade());
        return IngredienteConsulta.de(atualizado, compartimentoAtualizado);
    }

    /**
     * Remove espaços e recusa texto vazio.
     *
     * @param valor texto recebido
     * @param mensagem mensagem usada quando o texto é vazio
     * @return texto sem espaços nas extremidades
     */
    private String exigirTexto(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new RegraNegocioException(mensagem);
        }
        return valor.trim();
    }
}
