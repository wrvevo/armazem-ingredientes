package br.com.vega.armazem.adapter.in.web;

import br.com.vega.armazem.adapter.in.web.dto.ErroResponse;
import br.com.vega.armazem.domain.exception.RecursoNaoEncontradoException;
import br.com.vega.armazem.domain.exception.RegraNegocioException;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Converte exceções em respostas HTTP com mensagem clara e registra o ocorrido no Log4j.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(ApiExceptionHandler.class);

    private final MeterRegistry meterRegistry;

    /**
     * Recebe o registro usado para contar violações de regra.
     *
     * @param meterRegistry métricas da aplicação
     */
    public ApiExceptionHandler(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Responde 400 quando uma regra de armazenamento é violada.
     *
     * @param excecao regra recusada pelo domínio
     * @param request requisição atual
     * @return erro com a mensagem da regra
     */
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> regraDeNegocio(RegraNegocioException excecao, HttpServletRequest request) {
        meterRegistry.counter("armazem.regras.violadas").increment();
        LOGGER.warn("Regra de negócio recusada em {}: {}", request.getRequestURI(), excecao.getMessage());
        return responder(HttpStatus.BAD_REQUEST, "Requisição inválida", excecao.getMessage(), request);
    }

    /**
     * Responde 404 quando o ingrediente ou o compartimento não existe.
     *
     * @param excecao recurso ausente
     * @param request requisição atual
     * @return erro de recurso não encontrado
     */
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(RecursoNaoEncontradoException excecao, HttpServletRequest request) {
        LOGGER.warn("Recurso não encontrado em {}: {}", request.getRequestURI(), excecao.getMessage());
        return responder(HttpStatus.NOT_FOUND, "Não encontrado", excecao.getMessage(), request);
    }

    /**
     * Responde 400 com os campos que falharam na validação do corpo.
     *
     * @param excecao resultado da validação
     * @param request requisição atual
     * @return erros de campo reunidos em uma mensagem
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> corpoInvalido(MethodArgumentNotValidException excecao, HttpServletRequest request) {
        String mensagem = excecao.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));
        LOGGER.warn("Corpo inválido em {}: {}", request.getRequestURI(), mensagem);
        return responder(HttpStatus.BAD_REQUEST, "Requisição inválida", mensagem, request);
    }

    /**
     * Responde 400 quando um parâmetro obrigatório da consulta não foi enviado.
     *
     * @param excecao parâmetro ausente
     * @param request requisição atual
     * @return erro indicando o parâmetro
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResponse> parametroAusente(MissingServletRequestParameterException excecao,
                                                        HttpServletRequest request) {
        String mensagem = "O parâmetro " + excecao.getParameterName() + " é obrigatório.";
        LOGGER.warn("Parâmetro ausente em {}: {}", request.getRequestURI(), mensagem);
        return responder(HttpStatus.BAD_REQUEST, "Requisição inválida", mensagem, request);
    }

    /**
     * Responde 400 quando o JSON não pode ser lido.
     *
     * @param excecao falha de leitura
     * @param request requisição atual
     * @return erro de JSON inválido
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> jsonInvalido(HttpMessageNotReadableException excecao, HttpServletRequest request) {
        LOGGER.warn("JSON inválido em {}: {}", request.getRequestURI(), excecao.getMessage());
        return responder(HttpStatus.BAD_REQUEST, "Requisição inválida", "O JSON enviado não pôde ser lido.", request);
    }

    /**
     * Responde 401 quando o login recebe usuário ou senha incorretos.
     *
     * @param excecao credencial recusada
     * @param request requisição atual
     * @return erro de autenticação
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroResponse> credencialInvalida(BadCredentialsException excecao, HttpServletRequest request) {
        LOGGER.warn("Falha de login em {}", request.getRequestURI());
        return responder(HttpStatus.UNAUTHORIZED, "Não autenticado", "Usuário ou senha inválidos.", request);
    }

    /**
     * Responde 500 e registra a pilha quando ocorre uma falha inesperada.
     *
     * @param excecao falha não tratada
     * @param request requisição atual
     * @return erro genérico, sem detalhes internos
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> erroInterno(Exception excecao, HttpServletRequest request) {
        LOGGER.error("Falha inesperada em {}", request.getRequestURI(), excecao);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Erro interno ao processar a solicitação.", request);
    }

    /**
     * Monta o corpo padrão e o status HTTP.
     *
     * @param status código HTTP
     * @param erro título curto
     * @param mensagem explicação
     * @param request requisição de origem
     * @return resposta pronta
     */
    private ResponseEntity<ErroResponse> responder(HttpStatus status, String erro, String mensagem,
                                                   HttpServletRequest request) {
        ErroResponse corpo = new ErroResponse(LocalDateTime.now(), status.value(), erro, mensagem, request.getRequestURI());
        return ResponseEntity.status(status).body(corpo);
    }
}
