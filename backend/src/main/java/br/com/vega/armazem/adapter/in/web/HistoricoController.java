package br.com.vega.armazem.adapter.in.web;

import br.com.vega.armazem.adapter.in.web.dto.MovimentacaoResponse;
import br.com.vega.armazem.domain.port.in.ConsultarHistoricoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Expõe o histórico de entradas e saídas.
 */
@RestController
@RequestMapping("/historico")
@Tag(name = "Histórico", description = "Movimentações ordenadas por data ou compartimento")
@SecurityRequirement(name = "bearer-jwt")
public class HistoricoController {

    private final ConsultarHistoricoUseCase consultarHistorico;

    /**
     * Recebe o caso de uso do histórico.
     *
     * @param consultarHistorico consulta ordenada das movimentações
     */
    public HistoricoController(ConsultarHistoricoUseCase consultarHistorico) {
        this.consultarHistorico = consultarHistorico;
    }

    /**
     * Lista as movimentações, por padrão da data mais antiga para a mais nova.
     *
     * @param sortBy date ou compartimento
     * @param order asc ou desc
     * @return histórico ordenado
     */
    @GetMapping
    @Operation(summary = "Lista o histórico de movimentações")
    public List<MovimentacaoResponse> listar(@RequestParam(defaultValue = "date") String sortBy,
                                             @RequestParam(defaultValue = "asc") String order) {
        return consultarHistorico.listar(sortBy, order).stream().map(RespostaMapper::movimentacao).toList();
    }
}
