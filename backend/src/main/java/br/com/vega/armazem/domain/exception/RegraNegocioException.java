package br.com.vega.armazem.domain.exception;

/**
 * Sinaliza que a operação viola uma regra de armazenamento e deve responder 400.
 */
public class RegraNegocioException extends RuntimeException {

    /**
     * Cria a exceção com a mensagem que será devolvida ao cliente.
     *
     * @param mensagem explicação clara da regra violada
     */
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
