package br.com.vega.armazem.adapter.out.persistence;

import br.com.vega.armazem.adapter.out.persistence.entity.IngredienteEntity;
import br.com.vega.armazem.adapter.out.persistence.mapper.IngredienteMapper;
import br.com.vega.armazem.adapter.out.persistence.repository.IngredienteJpaRepository;
import br.com.vega.armazem.domain.exception.RecursoNaoEncontradoException;
import br.com.vega.armazem.domain.model.Ingrediente;
import br.com.vega.armazem.domain.model.TipoIngrediente;
import br.com.vega.armazem.domain.port.out.IngredientePort;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador JPA da porta de ingredientes.
 */
@Repository
public class IngredienteJpaAdapter implements IngredientePort {

    private final IngredienteJpaRepository repository;

    /**
     * Recebe o repositório Spring Data.
     *
     * @param repository repositório de ingredientes
     */
    public IngredienteJpaAdapter(IngredienteJpaRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Ingrediente salvar(Ingrediente ingrediente) {
        IngredienteEntity entity = ingrediente.getId() == null
                ? new IngredienteEntity()
                : repository.findById(ingrediente.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Ingrediente " + ingrediente.getId() + " não foi encontrado."));
        IngredienteMapper.copiar(ingrediente, entity);
        return IngredienteMapper.paraDominio(repository.save(entity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Ingrediente> buscarPorId(Long id) {
        return repository.findById(id).map(IngredienteMapper::paraDominio);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Ingrediente> buscarPorIdComBloqueio(Long id) {
        return repository.buscarParaAtualizacao(id).map(IngredienteMapper::paraDominio);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ingrediente> listar() {
        return repository.findAll().stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .map(IngredienteMapper::paraDominio)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<TipoIngrediente, BigDecimal> somarPorTipo() {
        return repository.somarPorTipo().stream()
                .collect(Collectors.toMap(
                        linha -> (TipoIngrediente) linha[0],
                        linha -> new BigDecimal(linha[1].toString())));
    }
}
