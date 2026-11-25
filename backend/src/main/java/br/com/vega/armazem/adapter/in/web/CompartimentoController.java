package br.com.vega.armazem.adapter.in.web;

import br.com.vega.armazem.adapter.in.web.dto.CompartimentoResponse;
import br.com.vega.armazem.adapter.in.web.dto.DisponibilidadeResponse;
import br.com.vega.armazem.domain.model.TipoIngrediente;
import br.com.vega.armazem.domain.port.in.ConsultarCompartimentoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * Expõe o estado dos compartimentos e as consultas de espaço e venda.
 */
@RestController
@RequestMapping("/compartimentos")
@Tag(name = "Compartimentos", description = "Estado, espaço livre e disponibilidade para venda")
@SecurityRequirement(name = "bearer-jwt")
public class CompartimentoController {

    private final ConsultarCompartimentoUseCase consultarCompartimento;

    /**
     * Recebe o caso de uso de compartimentos.
     *
     * @param consultarCompartimento consultas de espaço e estoque
     */
    public CompartimentoController(ConsultarCompartimentoUseCase consultarCompartimento) {
        this.consultarCompartimento = consultarCompartimento;
    }

    /**
     * Lista os cinco compartimentos e o saldo de cada um.
     *
     * @return C1 a C5
     */
    @GetMapping
    @Operation(summary = "Lista os compartimentos")
    public List<CompartimentoResponse> listar() {
        return consultarCompartimento.listar().stream().map(RespostaMapper::compartimento).toList();
    }

    /**
     * Informa quais compartimentos comportam a quantidade e o tipo pedidos.
     *
     * @param quantidade quantidade que se pretende armazenar
     * @param tipo tipo do ingrediente
     * @return compartimentos com espaço livre suficiente
     */
    @GetMapping("/disponiveis")
    @Operation(summary = "Lista compartimentos com espaço livre suficiente")
    public List<DisponibilidadeResponse> disponiveis(@RequestParam BigDecimal quantidade, @RequestParam String tipo) {
        return consultarCompartimento.listarDisponiveis(quantidade, TipoIngrediente.interpretar(tipo)).stream()
                .map(RespostaMapper::disponibilidade)
                .toList();
    }

    /**
     * Informa quais compartimentos ainda têm saldo do tipo pedido.
     *
     * @param tipo tipo que se pretende retirar ou vender
     * @return compartimentos com quantidade maior que zero
     */
    @GetMapping("/disponiveis-para-venda")
    @Operation(summary = "Lista compartimentos com saldo para retirada")
    public List<DisponibilidadeResponse> disponiveisParaVenda(@RequestParam String tipo) {
        return consultarCompartimento.listarDisponiveisParaVenda(TipoIngrediente.interpretar(tipo)).stream()
                .map(RespostaMapper::disponibilidade)
                .toList();
    }
}
