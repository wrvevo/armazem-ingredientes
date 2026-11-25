package br.com.vega.armazem.adapter.out.persistence.repository;

import br.com.vega.armazem.adapter.out.persistence.entity.IngredienteEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA dos ingredientes.
 */
public interface IngredienteJpaRepository extends JpaRepository<IngredienteEntity, Long> {

    /**
     * Carrega o ingrediente com bloqueio pessimista de escrita.
     *
     * @param id identificador do ingrediente
     * @return entidade bloqueada até o fim da transação
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from IngredienteEntity i where i.id = :id")
    Optional<IngredienteEntity> buscarParaAtualizacao(@Param("id") Long id);

    /**
     * Soma a quantidade de cada tipo em uma única consulta.
     *
     * @return pares de tipo e soma
     */
    @Query("select i.tipo, sum(i.quantidade) from IngredienteEntity i group by i.tipo")
    List<Object[]> somarPorTipo();
}
