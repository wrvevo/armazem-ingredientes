package br.com.vega.armazem.config;

import br.com.vega.armazem.adapter.out.persistence.entity.CompartimentoEntity;
import br.com.vega.armazem.adapter.out.persistence.entity.UsuarioEntity;
import br.com.vega.armazem.adapter.out.persistence.repository.CompartimentoJpaRepository;
import br.com.vega.armazem.adapter.out.persistence.repository.UsuarioJpaRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Cria os cinco compartimentos e os usuários de demonstração quando o banco está vazio.
 */
@Component
public class DadosIniciais implements ApplicationRunner {

    private static final Logger LOGGER = LogManager.getLogger(DadosIniciais.class);

    private final UsuarioJpaRepository usuarioRepository;
    private final CompartimentoJpaRepository compartimentoRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Recebe os repositórios e o codificador de senha.
     *
     * @param usuarioRepository repositório de usuários
     * @param compartimentoRepository repositório de compartimentos
     * @param passwordEncoder codificador BCrypt
     */
    public DadosIniciais(UsuarioJpaRepository usuarioRepository, CompartimentoJpaRepository compartimentoRepository,
                         PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.compartimentoRepository = compartimentoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Insere usuários e compartimentos apenas na primeira subida do banco.
     *
     * @param args argumentos da aplicação, não utilizados
     */
    @Override
    public void run(ApplicationArguments args) {
        criarUsuarios();
        criarCompartimentos();
    }

    /**
     * Grava o administrador e o operador de demonstração.
     */
    private void criarUsuarios() {
        if (usuarioRepository.count() > 0) {
            return;
        }
        usuarioRepository.save(new UsuarioEntity("admin", passwordEncoder.encode("admin123"), "ROLE_ADMIN"));
        usuarioRepository.save(new UsuarioEntity("pedro", passwordEncoder.encode("pedro123"), "ROLE_OPERADOR"));
        LOGGER.info("Usuários iniciais criados: admin e pedro.");
    }

    /**
     * Grava os compartimentos C1 a C5 vazios.
     */
    private void criarCompartimentos() {
        if (compartimentoRepository.count() > 0) {
            return;
        }
        for (int indice = 1; indice <= 5; indice++) {
            CompartimentoEntity compartimento = new CompartimentoEntity();
            compartimento.setId((long) indice);
            compartimento.setCodigo("C" + indice);
            compartimento.setQuantidadeAtual(new BigDecimal("0.000"));
            compartimentoRepository.save(compartimento);
        }
        LOGGER.info("Compartimentos iniciais criados: C1, C2, C3, C4 e C5.");
    }
}
