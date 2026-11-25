package br.com.vega.armazem.adapter.out.persistence.repository;

import br.com.vega.armazem.adapter.out.persistence.entity.MovimentacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório JPA do histórico de movimentações.
 */
public interface MovimentacaoJpaRepository extends JpaRepository<MovimentacaoEntity, Long> {
}
