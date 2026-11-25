package br.com.vega.armazem.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Registra no Log4j o método, o caminho, o status e a duração de cada requisição.
 */
public class FiltroDeAuditoria extends OncePerRequestFilter {

    private static final Logger LOGGER = LogManager.getLogger(FiltroDeAuditoria.class);

    /**
     * Mede a requisição e escolhe o nível de log conforme o status HTTP.
     *
     * @param request requisição recebida
     * @param response resposta produzida
     * @param filterChain continuação da cadeia de filtros
     * @throws ServletException quando um filtro seguinte falha
     * @throws IOException quando a escrita da resposta falha
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        long inicio = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            registrar(request, response.getStatus(), System.currentTimeMillis() - inicio);
        }
    }

    /**
     * Grava informação, alerta ou erro sem incluir cabeçalhos sensíveis.
     *
     * @param request requisição concluída
     * @param status código HTTP final
     * @param duracaoMillis tempo gasto em milissegundos
     */
    private void registrar(HttpServletRequest request, int status, long duracaoMillis) {
        String mensagem = "Requisição {} {} status={} duracao={}ms";
        if (status >= 500) {
            LOGGER.error(mensagem, request.getMethod(), request.getRequestURI(), status, duracaoMillis);
        } else if (status >= 400) {
            LOGGER.warn(mensagem, request.getMethod(), request.getRequestURI(), status, duracaoMillis);
        } else {
            LOGGER.info(mensagem, request.getMethod(), request.getRequestURI(), status, duracaoMillis);
        }
    }
}
