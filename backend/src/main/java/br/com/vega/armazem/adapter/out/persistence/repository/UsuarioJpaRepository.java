package br.com.vega.armazem.adapter.out.persistence.repository;

import br.com.vega.armazem.adapter.out.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório JPA dos usuários de autenticação.
 */
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {

    /**
     * Localiza o usuário pelo nome de login.
     *
     * @param usuario nome informado na tela de login
     * @return usuário encontrado, se existir
     */
    Optional<UsuarioEntity> findByUsuario(String usuario);
}
