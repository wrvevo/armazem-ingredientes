package br.com.vega.armazem.domain.port.out;

import br.com.vega.armazem.domain.model.Ingrediente;
import br.com.vega.armazem.domain.model.TipoIngrediente;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Porta de saída para o saldo dos ingredientes.
 */
public interface IngredientePort {

    /**
     * Insere ou atualiza o saldo de um ingrediente.
     *
     * @param ingrediente estado a gravar
     * @return ingrediente com identificador
     */
    Ingrediente salvar(Ingrediente ingrediente);

    /**
     * Busca um ingrediente sem bloqueio.
     *
     * @param id identificador do ingrediente
     * @return ingrediente encontrado, se existir
     */
    Optional<Ingrediente> buscarPorId(Long id);

    /**
     * Busca o ingrediente e mantém bloqueio pessimista até o fim da transação.
     *
     * @param id identificador do ingrediente
     * @return ingrediente bloqueado para atualização
     */
    Optional<Ingrediente> buscarPorIdComBloqueio(Long id);

    /**
     * Lista todos os ingredientes cadastrados.
     *
     * @return saldos ordenados pelo identificador
     */
    List<Ingrediente> listar();

    /**
     * Soma o saldo de cada tipo em uma única consulta.
     *
     * @return mapa do tipo para a quantidade total; tipos sem saldo podem estar ausentes
     */
    Map<TipoIngrediente, BigDecimal> somarPorTipo();
}
