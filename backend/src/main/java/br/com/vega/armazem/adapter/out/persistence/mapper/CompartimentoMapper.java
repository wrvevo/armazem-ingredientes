package br.com.vega.armazem.adapter.out.persistence.mapper;

import br.com.vega.armazem.adapter.out.persistence.entity.CompartimentoEntity;
import br.com.vega.armazem.domain.model.Compartimento;

/**
 * Converte o compartimento entre o domínio e a entidade JPA.
 */
public final class CompartimentoMapper {

    /**
     * Impede a instanciação desta classe de funções.
     */
    private CompartimentoMapper() {
    }

    /**
     * Cria o objeto de domínio a partir da linha persistida.
     *
     * @param entity entidade gerenciada
     * @return compartimento de domínio
     */
    public static Compartimento paraDominio(CompartimentoEntity entity) {
        return new Compartimento(entity.getId(), entity.getCodigo(), entity.getTipoAtual(),
                entity.getQuantidadeAtual(), entity.getDataUltimoArmazenamentoSeco());
    }

    /**
     * Copia o estado de domínio para a entidade já gerenciada.
     *
     * @param compartimento estado calculado
     * @param entity entidade que será sincronizada
     */
    public static void copiar(Compartimento compartimento, CompartimentoEntity entity) {
        entity.setCodigo(compartimento.getCodigo());
        entity.setTipoAtual(compartimento.getTipoAtual());
        entity.setQuantidadeAtual(compartimento.getQuantidadeAtual());
        entity.setDataUltimoArmazenamentoSeco(compartimento.getDataUltimoArmazenamentoSeco());
    }
}
