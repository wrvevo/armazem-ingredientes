package br.com.vega.armazem.domain.port.in;

import br.com.vega.armazem.domain.model.Compartimento;
import br.com.vega.armazem.domain.model.Disponibilidade;
import br.com.vega.armazem.domain.model.TipoIngrediente;

import java.math.BigDecimal;
import java.util.List;

/**
 * Caso de uso das consultas de compartimento e espaço livre.
 */
public interface ConsultarCompartimentoUseCase {

    /**
     * Lista o estado atual dos cinco compartimentos.
     *
     * @return compartimentos C1 a C5
     */
    List<Compartimento> listar();

    /**
     * Seleciona compartimentos com espaço livre suficiente para a quantidade e o tipo.
     *
     * @param quantidade quantidade que se pretende guardar
     * @param tipo tipo desejado
     * @return compartimentos em que a entrada caberia agora
     */
    List<Disponibilidade> listarDisponiveis(BigDecimal quantidade, TipoIngrediente tipo);

    /**
     * Seleciona compartimentos que ainda têm saldo do tipo pedido.
     *
     * @param tipo tipo que se pretende retirar ou vender
     * @return compartimentos com quantidade maior que zero daquele tipo
     */
    List<Disponibilidade> listarDisponiveisParaVenda(TipoIngrediente tipo);
}
