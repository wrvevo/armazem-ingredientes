package br.com.vega.armazem.domain.exception;

/**
 * Sinaliza que o recurso pedido não existe e deve responder 404.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    /**
     * Cria a exceção com a mensagem que será devolvida ao cliente.
     *
     * @param mensagem identificação do recurso ausente
     */
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
