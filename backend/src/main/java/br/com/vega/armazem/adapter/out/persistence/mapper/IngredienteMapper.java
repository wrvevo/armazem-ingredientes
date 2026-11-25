package br.com.vega.armazem.adapter.out.persistence.mapper;

import br.com.vega.armazem.adapter.out.persistence.entity.IngredienteEntity;
import br.com.vega.armazem.domain.model.Ingrediente;

/**
 * Converte o ingrediente entre o domínio e a entidade JPA.
 */
public final class IngredienteMapper {

    /**
     * Impede a instanciação desta classe de funções.
     */
    private IngredienteMapper() {
    }

    /**
     * Cria o objeto de domínio a partir da linha persistida.
     *
     * @param entity entidade gerenciada
     * @return ingrediente de domínio
     */
    public static Ingrediente paraDominio(IngredienteEntity entity) {
        return new Ingrediente(entity.getId(), entity.getNome(), entity.getTipo(),
                entity.getQuantidade(), entity.getCompartimentoId());
    }

    /**
     * Copia o estado de domínio para a entidade nova ou já gerenciada.
     *
     * @param ingrediente estado calculado
     * @param entity entidade que será sincronizada
     */
    public static void copiar(Ingrediente ingrediente, IngredienteEntity entity) {
        entity.setNome(ingrediente.getNome());
        entity.setTipo(ingrediente.getTipo());
        entity.setQuantidade(ingrediente.getQuantidade());
        entity.setCompartimentoId(ingrediente.getCompartimentoId());
    }
}
