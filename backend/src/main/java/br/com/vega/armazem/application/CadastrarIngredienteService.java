package br.com.vega.armazem.application;

import br.com.vega.armazem.domain.exception.RecursoNaoEncontradoException;
import br.com.vega.armazem.domain.exception.RegraNegocioException;
import br.com.vega.armazem.domain.model.Compartimento;
import br.com.vega.armazem.domain.model.Ingrediente;
import br.com.vega.armazem.domain.model.IngredienteConsulta;
import br.com.vega.armazem.domain.model.Movimentacao;
import br.com.vega.armazem.domain.model.TipoIngrediente;
import br.com.vega.armazem.domain.port.in.CadastrarIngredienteUseCase;
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
 * Orquestra a entrada de estoque com bloqueio do compartimento e histórico obrigatório.
 */
@Service
public class CadastrarIngredienteService implements CadastrarIngredienteUseCase {

    private static final Logger LOGGER = LogManager.getLogger(CadastrarIngredienteService.class);

    private final CompartimentoPort compartimentoPort;
    private final IngredientePort ingredientePort;
    private final HistoricoPort historicoPort;
    private final Clock relogio;
    private final Counter entradas;

    /**
     * Recebe as portas, o relógio e o registro de métricas da entrada.
     *
     * @param compartimentoPort porta dos compartimentos
     * @param ingredientePort porta dos ingredientes
     * @param historicoPort porta do histórico
     * @param relogio relógio usado na data da movimentação
     * @param meterRegistry registro de métricas do Micrometer
     */
    public CadastrarIngredienteService(CompartimentoPort compartimentoPort, IngredientePort ingredientePort,
                                       HistoricoPort historicoPort, Clock relogio, MeterRegistry meterRegistry) {
        this.compartimentoPort = compartimentoPort;
        this.ingredientePort = ingredientePort;
        this.historicoPort = historicoPort;
        this.relogio = relogio;
        this.entradas = meterRegistry.counter("armazem.movimentacoes", "operacao", "ENTRADA");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public IngredienteConsulta cadastrar(String nome, TipoIngrediente tipo, BigDecimal quantidade,
                                         Long compartimentoId, String responsavel) {
        String nomeLimpo = exigirTexto(nome, "O nome do ingrediente é obrigatório.");
        String responsavelLimpo = exigirTexto(responsavel, "O responsável pela movimentação é obrigatório.");
        Compartimento compartimento = compartimentoPort.buscarPorIdComBloqueio(compartimentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Compartimento " + compartimentoId + " não foi encontrado."));
        Compartimento atualizado = compartimento.registrarEntrada(tipo, quantidade, LocalDateTime.now(relogio).toLocalDate());
        Ingrediente salvo = ingredientePort.salvar(new Ingrediente(null, nomeLimpo, tipo, quantidade, compartimento.getId()));
        compartimentoPort.salvar(atualizado);
        historicoPort.registrar(Movimentacao.entrada(LocalDateTime.now(relogio), salvo, quantidade, responsavelLimpo, atualizado));
        entradas.increment();
        LOGGER.info("Entrada registrada: ingrediente={} tipo={} quantidade={} compartimento={} responsavel={}",
                salvo.getNome(), tipo, salvo.getQuantidade(), atualizado.getCodigo(), responsavelLimpo);
        return IngredienteConsulta.de(salvo, atualizado);
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
