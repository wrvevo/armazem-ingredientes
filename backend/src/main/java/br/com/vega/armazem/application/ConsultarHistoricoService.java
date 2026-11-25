package br.com.vega.armazem.application;

import br.com.vega.armazem.domain.exception.RegraNegocioException;
import br.com.vega.armazem.domain.model.Movimentacao;
import br.com.vega.armazem.domain.port.in.ConsultarHistoricoUseCase;
import br.com.vega.armazem.domain.port.out.HistoricoPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Valida a ordenação pedida e devolve o histórico correspondente.
 */
@Service
public class ConsultarHistoricoService implements ConsultarHistoricoUseCase {

    private final HistoricoPort historicoPort;

    /**
     * Recebe a porta do histórico.
     *
     * @param historicoPort porta de leitura das movimentações
     */
    public ConsultarHistoricoService(HistoricoPort historicoPort) {
        this.historicoPort = historicoPort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Movimentacao> listar(String sortBy, String order) {
        return historicoPort.listar(campoDeOrdenacao(sortBy), direcaoDeOrdenacao(order));
    }

    /**
     * Traduz o parâmetro sortBy para a propriedade persistida.
     *
     * @param sortBy valor recebido na consulta
     * @return dataHora ou compartimentoId
     */
    private String campoDeOrdenacao(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "dataHora";
        }
        String normalizado = sortBy.trim().toLowerCase(Locale.ROOT);
        return switch (normalizado) {
            case "date", "data", "datahora" -> "dataHora";
            case "compartimento", "compartment" -> "compartimentoId";
            default -> throw new RegraNegocioException(
                    "Ordenação inválida. Use sortBy=date ou sortBy=compartimento.");
        };
    }

    /**
     * Traduz o parâmetro order para a direção aceita pelo repositório.
     *
     * @param order asc ou desc
     * @return ASC ou DESC
     */
    private String direcaoDeOrdenacao(String order) {
        if (order == null || order.isBlank() || order.equalsIgnoreCase("asc")) {
            return "ASC";
        }
        if (order.equalsIgnoreCase("desc")) {
            return "DESC";
        }
        throw new RegraNegocioException("Ordem inválida. Use order=asc ou order=desc.");
    }
}
