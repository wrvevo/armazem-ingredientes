package br.com.vega.armazem.domain.port.out;

import br.com.vega.armazem.domain.model.Compartimento;

import java.util.List;
import java.util.Optional;

/**
 * Porta de saída para persistir e bloquear compartimentos.
 */
public interface CompartimentoPort {

    /**
     * Busca um compartimento sem bloqueio, para consultas.
     *
     * @param id identificador do compartimento
     * @return compartimento encontrado, se existir
     */
    Optional<Compartimento> buscarPorId(Long id);

    /**
     * Busca o compartimento e mantém bloqueio pessimista até o fim da transação.
     *
     * @param id identificador do compartimento
     * @return compartimento bloqueado para atualização
     */
    Optional<Compartimento> buscarPorIdComBloqueio(Long id);

    /**
     * Lista os cinco compartimentos na ordem dos códigos.
     *
     * @return compartimentos C1 a C5
     */
    List<Compartimento> listar();

    /**
     * Grava o novo estado do compartimento.
     *
     * @param compartimento estado calculado pelo domínio
     * @return estado persistido
     */
    Compartimento salvar(Compartimento compartimento);
}
