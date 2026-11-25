package br.com.vega.armazem.adapter.in.web;

import br.com.vega.armazem.adapter.in.web.dto.LoginRequest;
import br.com.vega.armazem.adapter.in.web.dto.LoginResponse;
import br.com.vega.armazem.config.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Autentica o operador e devolve o token usado nas demais rotas.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Login com Spring Security e emissão de JWT")
public class AuthController {

    private static final Logger LOGGER = LogManager.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Recebe o gerenciador de autenticação e o emissor de tokens.
     *
     * @param authenticationManager verificador de usuário e senha
     * @param jwtService serviço de JWT
     */
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Confere as credenciais e devolve o token Bearer.
     *
     * @param requisicao usuário e senha
     * @return token, prazo e papéis
     */
    @PostMapping("/login")
    @Operation(summary = "Autentica um usuário", security = {})
    public LoginResponse login(@Valid @RequestBody LoginRequest requisicao) {
        Authentication autenticacao = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requisicao.usuario(), requisicao.senha()));
        String token = jwtService.gerar((org.springframework.security.core.userdetails.UserDetails) autenticacao.getPrincipal());
        LOGGER.info("Login realizado para o usuário {}", requisicao.usuario());
        return new LoginResponse(token, "Bearer", jwtService.getExpiracaoMinutos(), autenticacao.getName(),
                autenticacao.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }
}
