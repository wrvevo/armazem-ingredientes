package br.com.vega.armazem.domain.port.in;

import br.com.vega.armazem.domain.model.IngredienteConsulta;

import java.util.List;

/**
 * Caso de uso de consulta dos ingredientes cadastrados.
 */
public interface ConsultarIngredienteUseCase {

    /**
     * Lista todos os ingredientes e o compartimento de cada um.
     *
     * @return saldos cadastrados
     */
    List<IngredienteConsulta> listar();

    /**
     * Busca um ingrediente pelo identificador.
     *
     * @param id identificador do ingrediente
     * @return saldo encontrado
     */
    IngredienteConsulta buscarPorId(Long id);
}
