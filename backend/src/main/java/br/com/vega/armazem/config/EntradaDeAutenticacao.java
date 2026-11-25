package br.com.vega.armazem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Responde 401 em JSON quando a rota exige autenticação e o token não foi aceito.
 */
@Component
public class EntradaDeAutenticacao implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * Recebe o serializador JSON da aplicação.
     *
     * @param objectMapper mapeador configurado pelo Spring
     */
    public EntradaDeAutenticacao(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Escreve o corpo de erro de autenticação e o status 401.
     *
     * @param request requisição rejeitada
     * @param response resposta ainda aberta
     * @param authException motivo da rejeição
     * @throws IOException quando a resposta não pode ser escrita
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        escrever(response, request, 401, "Não autenticado",
                "Informe um token Bearer válido obtido em POST /auth/login.");
    }

    /**
     * Serializa um erro HTTP no mesmo formato do tratador da API.
     *
     * @param response resposta HTTP
     * @param request requisição de origem
     * @param status código HTTP
     * @param erro título curto
     * @param mensagem explicação para o cliente
     * @throws IOException quando a resposta não pode ser escrita
     */
    public void escrever(HttpServletResponse response, HttpServletRequest request, int status,
                         String erro, String mensagem) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), new ErroJson(
                LocalDateTime.now(), status, erro, mensagem, request.getRequestURI()));
    }

    /**
     * Corpo JSON produzido fora do ciclo do controlador.
     *
     * @param timestamp instante do erro
     * @param status código HTTP
     * @param erro título curto
     * @param mensagem explicação
     * @param caminho caminho da requisição
     */
    public record ErroJson(LocalDateTime timestamp, int status, String erro, String mensagem, String caminho) {
    }
}
