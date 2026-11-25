package br.com.vega.armazem.domain.port.in;

import br.com.vega.armazem.domain.model.IngredienteConsulta;

import java.math.BigDecimal;

/**
 * Caso de uso de retirada de ingrediente.
 */
public interface RegistrarSaidaUseCase {

    /**
     * Baixa o saldo do ingrediente, atualiza o compartimento e grava a saída no histórico.
     *
     * @param ingredienteId ingrediente retirado
     * @param quantidade quantidade positiva
     * @param responsavel pessoa responsável pela saída
     * @return saldo restante com o código do compartimento
     */
    IngredienteConsulta registrar(Long ingredienteId, BigDecimal quantidade, String responsavel);
}
