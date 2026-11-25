package br.com.vega.armazem.domain.port.in;

import br.com.vega.armazem.domain.model.IngredienteConsulta;
import br.com.vega.armazem.domain.model.TipoIngrediente;

import java.math.BigDecimal;

/**
 * Caso de uso de entrada de ingrediente em um compartimento.
 */
public interface CadastrarIngredienteUseCase {

    /**
     * Cadastra o ingrediente, atualiza o compartimento e grava a entrada no histórico.
     *
     * @param nome nome comercial
     * @param tipo classificação já interpretada
     * @param quantidade quantidade positiva
     * @param compartimentoId compartimento de destino
     * @param responsavel pessoa responsável pela entrada
     * @return saldo cadastrado com o código do compartimento
     */
    IngredienteConsulta cadastrar(String nome, TipoIngrediente tipo, BigDecimal quantidade,
                                  Long compartimentoId, String responsavel);
}
