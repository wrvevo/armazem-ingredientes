package br.com.vega.armazem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Define a autenticação stateless, as rotas públicas e o CORS do painel.
 */
@Configuration
public class SecurityConfig {

    /**
     * Codifica senhas com BCrypt antes de gravá-las e ao autenticar.
     *
     * @return codificador de senha
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expõe o gerenciador usado pelo endpoint de login.
     *
     * @param configuration configuração montada pelo Spring Security
     * @return gerenciador de autenticação
     * @throws Exception quando a configuração não pode ser lida
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Monta o filtro JWT fora do registro automático de servlets.
     *
     * @param jwtService serviço de token
     * @param usuarioDetailsService consulta de usuários
     * @return filtro de autenticação
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService,
                                                           UsuarioDetailsService usuarioDetailsService) {
        return new JwtAuthenticationFilter(jwtService, usuarioDetailsService);
    }

    /**
     * Impede que o filtro JWT seja registrado duas vezes pelo contêiner de servlets.
     *
     * @param filtro filtro já criado para a cadeia do Spring Security
     * @return registro desativado
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtDesativadoNoContainer(JwtAuthenticationFilter filtro) {
        FilterRegistrationBean<JwtAuthenticationFilter> registro = new FilterRegistrationBean<>(filtro);
        registro.setEnabled(false);
        return registro;
    }

    /**
     * Publica o filtro de auditoria uma única vez, no início da cadeia.
     *
     * @return registro do filtro de log
     */
    @Bean
    public FilterRegistrationBean<FiltroDeAuditoria> filtroDeAuditoria() {
        FilterRegistrationBean<FiltroDeAuditoria> registro = new FilterRegistrationBean<>(new FiltroDeAuditoria());
        registro.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registro;
    }

    /**
     * Libera as origens do painel em desenvolvimento e no Docker.
     *
     * @param origens lista separada por vírgula vinda da configuração
     * @return fonte de CORS aplicada a todas as rotas
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.origens}") String origens) {
        CorsConfiguration configuracao = new CorsConfiguration();
        configuracao.setAllowedOrigins(Arrays.stream(origens.split(",")).map(String::trim).filter(item -> !item.isEmpty()).toList());
        configuracao.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuracao.setAllowedHeaders(List.of("*"));
        configuracao.setExposedHeaders(List.of("Authorization"));
        UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
        fonte.registerCorsConfiguration("/**", configuracao);
        return fonte;
    }

    /**
     * Protege a API, mantém Swagger, saúde e login públicos e desliga sessão e CSRF.
     * O CSRF fica desligado porque a API é stateless e autentica por Bearer token.
     *
     * @param http construtor da cadeia de segurança
     * @param filtroJwt filtro que lê o token
     * @param entradaDeAutenticacao resposta 401
     * @return cadeia pronta
     * @throws Exception quando a cadeia não pode ser construída
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter filtroJwt,
                                                   EntradaDeAutenticacao entradaDeAutenticacao) throws Exception {
        AccessDeniedHandler acessoNegado = (request, response, exception) ->
                entradaDeAutenticacao.escrever(response, request, 403, "Acesso negado",
                        "O usuário autenticado não pode executar esta operação.");
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(tratamento -> tratamento
                        .authenticationEntryPoint(entradaDeAutenticacao)
                        .accessDeniedHandler(acessoNegado))
                .authorizeHttpRequests(autorizacao -> autorizacao
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info", "/actuator/prometheus").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated())
                .headers(cabecalhos -> cabecalhos.frameOptions(frame -> frame.sameOrigin()))
                .addFilterBefore(filtroJwt, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
