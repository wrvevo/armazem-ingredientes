package br.com.vega.armazem.adapter.in.web;

import br.com.vega.armazem.adapter.in.web.dto.CadastroIngredienteRequest;
import br.com.vega.armazem.adapter.in.web.dto.IngredienteResponse;
import br.com.vega.armazem.adapter.in.web.dto.SaidaRequest;
import br.com.vega.armazem.adapter.in.web.dto.VolumeResponse;
import br.com.vega.armazem.domain.model.TipoIngrediente;
import br.com.vega.armazem.domain.port.in.CadastrarIngredienteUseCase;
import br.com.vega.armazem.domain.port.in.ConsultarIngredienteUseCase;
import br.com.vega.armazem.domain.port.in.ConsultarVolumeUseCase;
import br.com.vega.armazem.domain.port.in.RegistrarSaidaUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Expõe o cadastro, a consulta e a saída de ingredientes.
 */
@RestController
@RequestMapping("/ingredientes")
@Tag(name = "Ingredientes", description = "Cadastro, saldo, volume e saída")
@SecurityRequirement(name = "bearer-jwt")
public class IngredienteController {

    private final CadastrarIngredienteUseCase cadastrarIngrediente;
    private final ConsultarIngredienteUseCase consultarIngrediente;
    private final ConsultarVolumeUseCase consultarVolume;
    private final RegistrarSaidaUseCase registrarSaida;

    /**
     * Recebe os casos de uso de ingrediente.
     *
     * @param cadastrarIngrediente entrada de estoque
     * @param consultarIngrediente consultas de saldo
     * @param consultarVolume totais por tipo
     * @param registrarSaida baixa de estoque
     */
    public IngredienteController(CadastrarIngredienteUseCase cadastrarIngrediente,
                                 ConsultarIngredienteUseCase consultarIngrediente,
                                 ConsultarVolumeUseCase consultarVolume,
                                 RegistrarSaidaUseCase registrarSaida) {
        this.cadastrarIngrediente = cadastrarIngrediente;
        this.consultarIngrediente = consultarIngrediente;
        this.consultarVolume = consultarVolume;
        this.registrarSaida = registrarSaida;
    }

    /**
     * Cadastra um ingrediente e registra a entrada no histórico.
     *
     * @param requisicao dados do ingrediente e do compartimento
     * @return ingrediente criado
     */
    @PostMapping
    @Operation(summary = "Cadastra um ingrediente e registra a entrada")
    public ResponseEntity<IngredienteResponse> cadastrar(@Valid @RequestBody CadastroIngredienteRequest requisicao) {
        var criado = cadastrarIngrediente.cadastrar(requisicao.nome(), TipoIngrediente.interpretar(requisicao.tipo()),
                requisicao.quantidade(), requisicao.compartimentoId(), requisicao.responsavel());
        return ResponseEntity.created(URI.create("/ingredientes/" + criado.getId())).body(RespostaMapper.ingrediente(criado));
    }

    /**
     * Lista os ingredientes cadastrados.
     *
     * @return saldos atuais
     */
    @GetMapping
    @Operation(summary = "Lista os ingredientes")
    public List<IngredienteResponse> listar() {
        return consultarIngrediente.listar().stream().map(RespostaMapper::ingrediente).toList();
    }

    /**
     * Retorna o volume total de cada tipo.
     *
     * @return líquidos, refrigerados e secos, nesta ordem
     */
    @GetMapping("/volume")
    @Operation(summary = "Consulta o volume total por tipo")
    public List<VolumeResponse> volume() {
        return consultarVolume.listar().stream().map(RespostaMapper::volume).toList();
    }

    /**
     * Busca um ingrediente pelo identificador.
     *
     * @param id identificador do ingrediente
     * @return saldo encontrado
     */
    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Consulta um ingrediente pelo id")
    public IngredienteResponse buscar(@PathVariable Long id) {
        return RespostaMapper.ingrediente(consultarIngrediente.buscarPorId(id));
    }

    /**
     * Registra a saída de parte ou de todo o saldo do ingrediente.
     *
     * @param id identificador do ingrediente
     * @param requisicao quantidade e responsável
     * @return saldo restante
     */
    @PostMapping("/{id:\\d+}/saida")
    @Operation(summary = "Registra a saída de um ingrediente")
    public IngredienteResponse saida(@PathVariable Long id, @Valid @RequestBody SaidaRequest requisicao) {
        return RespostaMapper.ingrediente(registrarSaida.registrar(id, requisicao.quantidade(), requisicao.responsavel()));
    }
}
