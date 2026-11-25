package br.com.vega.armazem.adapter.out.persistence.mapper;

import br.com.vega.armazem.adapter.out.persistence.entity.MovimentacaoEntity;
import br.com.vega.armazem.domain.model.Movimentacao;

/**
 * Converte a movimentação entre o domínio e a entidade JPA.
 */
public final class MovimentacaoMapper {

    /**
     * Impede a instanciação desta classe de funções.
     */
    private MovimentacaoMapper() {
    }

    /**
     * Cria o objeto de domínio a partir da linha persistida.
     *
     * @param entity entidade gerenciada
     * @return movimentação de domínio
     */
    public static Movimentacao paraDominio(MovimentacaoEntity entity) {
        return new Movimentacao(entity.getId(), entity.getDataHora(), entity.getOperacao(), entity.getQuantidade(),
                entity.getTipo(), entity.getResponsavel(), entity.getCompartimentoId(), entity.getCompartimentoCodigo(),
                entity.getIngredienteId(), entity.getNomeIngrediente());
    }

    /**
     * Cria uma entidade nova a partir do lançamento de domínio.
     *
     * @param movimentacao lançamento ainda sem id
     * @return entidade pronta para inserção
     */
    public static MovimentacaoEntity paraEntidade(Movimentacao movimentacao) {
        MovimentacaoEntity entity = new MovimentacaoEntity();
        entity.setDataHora(movimentacao.getDataHora());
        entity.setOperacao(movimentacao.getOperacao());
        entity.setQuantidade(movimentacao.getQuantidade());
        entity.setTipo(movimentacao.getTipo());
        entity.setResponsavel(movimentacao.getResponsavel());
        entity.setCompartimentoId(movimentacao.getCompartimentoId());
        entity.setCompartimentoCodigo(movimentacao.getCompartimentoCodigo());
        entity.setIngredienteId(movimentacao.getIngredienteId());
        entity.setNomeIngrediente(movimentacao.getNomeIngrediente());
        return entity;
    }
}
