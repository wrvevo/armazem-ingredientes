package br.com.vega.armazem.adapter.out.persistence.repository;

import br.com.vega.armazem.adapter.out.persistence.entity.CompartimentoEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repositório JPA dos compartimentos.
 */
public interface CompartimentoJpaRepository extends JpaRepository<CompartimentoEntity, Long> {

    /**
     * Carrega o compartimento com bloqueio pessimista de escrita.
     *
     * @param id identificador do compartimento
     * @return entidade bloqueada até o fim da transação
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CompartimentoEntity c where c.id = :id")
    Optional<CompartimentoEntity> buscarParaAtualizacao(@Param("id") Long id);
}
