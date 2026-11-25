package br.com.vega.armazem.application;

import br.com.vega.armazem.domain.exception.RecursoNaoEncontradoException;
import br.com.vega.armazem.domain.model.Compartimento;
import br.com.vega.armazem.domain.model.Ingrediente;
import br.com.vega.armazem.domain.model.IngredienteConsulta;
import br.com.vega.armazem.domain.port.in.ConsultarIngredienteUseCase;
import br.com.vega.armazem.domain.port.out.CompartimentoPort;
import br.com.vega.armazem.domain.port.out.IngredientePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Consulta ingredientes e completa cada item com o código do compartimento.
 */
@Service
public class ConsultarIngredienteService implements ConsultarIngredienteUseCase {

    private final IngredientePort ingredientePort;
    private final CompartimentoPort compartimentoPort;

    /**
     * Recebe as portas de leitura de ingredientes e compartimentos.
     *
     * @param ingredientePort porta dos ingredientes
     * @param compartimentoPort porta dos compartimentos
     */
    public ConsultarIngredienteService(IngredientePort ingredientePort, CompartimentoPort compartimentoPort) {
        this.ingredientePort = ingredientePort;
        this.compartimentoPort = compartimentoPort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<IngredienteConsulta> listar() {
        Map<Long, Compartimento> compartimentos = mapaDeCompartimentos();
        return ingredientePort.listar().stream()
                .map(ingrediente -> montar(ingrediente, compartimentos))
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public IngredienteConsulta buscarPorId(Long id) {
        Ingrediente ingrediente = ingredientePort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ingrediente " + id + " não foi encontrado."));
        Compartimento compartimento = compartimentoPort.buscarPorId(ingrediente.getCompartimentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Compartimento " + ingrediente.getCompartimentoId() + " não foi encontrado."));
        return IngredienteConsulta.de(ingrediente, compartimento);
    }

    /**
     * Indexa os compartimentos pelo id para evitar uma consulta por ingrediente.
     *
     * @return mapa de id para compartimento
     */
    private Map<Long, Compartimento> mapaDeCompartimentos() {
        return compartimentoPort.listar().stream()
                .collect(Collectors.toMap(Compartimento::getId, Function.identity()));
    }

    /**
     * Une o ingrediente ao compartimento já carregado.
     *
     * @param ingrediente saldo encontrado
     * @param compartimentos compartimentos indexados
     * @return consulta pronta para a API
     */
    private IngredienteConsulta montar(Ingrediente ingrediente, Map<Long, Compartimento> compartimentos) {
        Compartimento compartimento = compartimentos.get(ingrediente.getCompartimentoId());
        if (compartimento == null) {
            throw new RecursoNaoEncontradoException(
                    "Compartimento " + ingrediente.getCompartimentoId() + " não foi encontrado.");
        }
        return IngredienteConsulta.de(ingrediente, compartimento);
    }
}
