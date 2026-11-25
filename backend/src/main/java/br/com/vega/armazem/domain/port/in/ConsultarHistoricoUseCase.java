package br.com.vega.armazem.domain.port.in;

import br.com.vega.armazem.domain.model.Movimentacao;

import java.util.List;

/**
 * Caso de uso da consulta do histórico de movimentações.
 */
public interface ConsultarHistoricoUseCase {

    /**
     * Lista as movimentações ordenadas pelos parâmetros aceitos pela API.
     *
     * @param sortBy date ou compartimento
     * @param order asc ou desc
     * @return histórico na ordem pedida
     */
    List<Movimentacao> listar(String sortBy, String order);
}
