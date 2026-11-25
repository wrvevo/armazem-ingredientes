package br.com.vega.armazem.config;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Converte um Bearer token válido no contexto de segurança da requisição.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LogManager.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UsuarioDetailsService usuarioDetailsService;

    /**
     * Recebe o serviço de token e a consulta de usuários.
     *
     * @param jwtService emissor e validador de JWT
     * @param usuarioDetailsService carga do usuário persistido
     */
    public JwtAuthenticationFilter(JwtService jwtService, UsuarioDetailsService usuarioDetailsService) {
        this.jwtService = jwtService;
        this.usuarioDetailsService = usuarioDetailsService;
    }

    /**
     * Autentica a requisição quando o cabeçalho Authorization traz um token aceito.
     *
     * @param request requisição HTTP
     * @param response resposta HTTP
     * @param filterChain continuação da cadeia
     * @throws ServletException quando um filtro seguinte falha
     * @throws IOException quando a leitura da requisição falha
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String cabecalho = request.getHeader("Authorization");
        if (cabecalho != null && cabecalho.startsWith("Bearer ")) {
            autenticar(request, cabecalho.substring(7));
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Preenche o contexto de segurança quando o token corresponde a um usuário ativo.
     *
     * @param request requisição atual
     * @param token JWT sem o prefixo Bearer
     */
    private void autenticar(HttpServletRequest request, String token) {
        try {
            String usuario = jwtService.extrairUsuario(token);
            if (usuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails detalhes = usuarioDetailsService.loadUserByUsername(usuario);
                UsernamePasswordAuthenticationToken autenticacao = new UsernamePasswordAuthenticationToken(
                        detalhes, null, detalhes.getAuthorities());
                autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(autenticacao);
            }
        } catch (JwtException | IllegalArgumentException excecao) {
            LOGGER.warn("Token JWT rejeitado: {}", excecao.getMessage());
        }
    }
}
