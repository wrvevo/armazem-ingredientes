package br.com.vega.armazem.config;

import br.com.vega.armazem.adapter.out.persistence.entity.UsuarioEntity;
import br.com.vega.armazem.adapter.out.persistence.repository.UsuarioJpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Carrega o usuário persistido para o mecanismo de autenticação.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioJpaRepository repository;

    /**
     * Recebe o repositório de usuários.
     *
     * @param repository repositório JPA
     */
    public UsuarioDetailsService(UsuarioJpaRepository repository) {
        this.repository = repository;
    }

    /**
     * Localiza o usuário e monta o UserDetails com a senha já codificada.
     *
     * @param username nome de login
     * @return detalhes aceitos pelo Spring Security
     * @throws UsernameNotFoundException quando o login não existe
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        UsuarioEntity usuario = repository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
        return User.withUsername(usuario.getUsuario())
                .password(usuario.getSenha())
                .authorities(usuario.getPapel())
                .build();
    }
}
