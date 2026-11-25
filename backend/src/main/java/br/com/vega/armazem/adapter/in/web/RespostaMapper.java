package br.com.vega.armazem.adapter.in.web;

import br.com.vega.armazem.adapter.in.web.dto.CompartimentoResponse;
import br.com.vega.armazem.adapter.in.web.dto.DisponibilidadeResponse;
import br.com.vega.armazem.adapter.in.web.dto.IngredienteResponse;
import br.com.vega.armazem.adapter.in.web.dto.MovimentacaoResponse;
import br.com.vega.armazem.adapter.in.web.dto.VolumeResponse;
import br.com.vega.armazem.domain.model.Compartimento;
import br.com.vega.armazem.domain.model.Disponibilidade;
import br.com.vega.armazem.domain.model.IngredienteConsulta;
import br.com.vega.armazem.domain.model.Movimentacao;
import br.com.vega.armazem.domain.model.TipoIngrediente;
import br.com.vega.armazem.domain.model.VolumePorTipo;

import java.math.BigDecimal;

/**
 * Traduz os objetos de domínio para os contratos JSON da API.
 */
public final class RespostaMapper {

    /**
     * Impede a instanciação desta classe de funções.
     */
    private RespostaMapper() {
    }

    /**
     * Converte a consulta de ingrediente no corpo da resposta.
     *
     * @param consulta saldo enriquecido
     * @return resposta JSON
     */
    public static IngredienteResponse ingrediente(IngredienteConsulta consulta) {
        return new IngredienteResponse(consulta.getId(), consulta.getNome(), consulta.getTipo().name(),
                consulta.getQuantidade(), consulta.getCompartimentoId(), consulta.getCompartimentoCodigo());
    }

    /**
     * Converte o total do domínio para o contrato type/totalQuantity.
     *
     * @param volume total de um tipo
     * @return resposta JSON
     */
    public static VolumeResponse volume(VolumePorTipo volume) {
        return new VolumeResponse(volume.getTipo().name(), volume.getQuantidadeTotal());
    }

    /**
     * Converte o compartimento, calculando a sobra apenas quando ele já tem um tipo.
     *
     * @param compartimento estado atual
     * @return resposta JSON
     */
    public static CompartimentoResponse compartimento(Compartimento compartimento) {
        TipoIngrediente tipo = compartimento.getTipoAtual();
        BigDecimal capacidade = tipo == null ? null : tipo.getCapacidadeMaxima();
        BigDecimal livre = tipo == null ? null : tipo.getCapacidadeMaxima().subtract(compartimento.getQuantidadeAtual());
        String unidade = tipo == null ? null : tipo.getUnidade();
        return new CompartimentoResponse(compartimento.getId(), compartimento.getCodigo(),
                tipo == null ? null : tipo.name(), compartimento.getQuantidadeAtual(), capacidade, livre, unidade,
                compartimento.getDataUltimoArmazenamentoSeco());
    }

    /**
     * Converte a disponibilidade calculada.
     *
     * @param disponibilidade resultado da consulta de espaço
     * @return resposta JSON
     */
    public static DisponibilidadeResponse disponibilidade(Disponibilidade disponibilidade) {
        String tipo = disponibilidade.getTipoArmazenado() == null ? null : disponibilidade.getTipoArmazenado().name();
        return new DisponibilidadeResponse(disponibilidade.getId(), disponibilidade.getCodigo(), tipo,
                disponibilidade.getQuantidadeAtual(), disponibilidade.getCapacidadeMaxima(),
                disponibilidade.getEspacoLivre(), disponibilidade.getUnidade());
    }

    /**
     * Converte um lançamento do histórico.
     *
     * @param movimentacao lançamento persistido
     * @return resposta JSON
     */
    public static MovimentacaoResponse movimentacao(Movimentacao movimentacao) {
        return new MovimentacaoResponse(movimentacao.getId(), movimentacao.getDataHora(),
                movimentacao.getOperacao().name(), movimentacao.getQuantidade(), movimentacao.getTipo().name(),
                movimentacao.getResponsavel(), movimentacao.getCompartimentoId(),
                movimentacao.getCompartimentoCodigo(), movimentacao.getIngredienteId(),
                movimentacao.getNomeIngrediente());
    }
}
