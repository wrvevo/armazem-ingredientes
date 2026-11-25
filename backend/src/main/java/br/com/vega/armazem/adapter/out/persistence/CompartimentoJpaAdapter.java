package br.com.vega.armazem.adapter.out.persistence;

import br.com.vega.armazem.adapter.out.persistence.entity.CompartimentoEntity;
import br.com.vega.armazem.adapter.out.persistence.mapper.CompartimentoMapper;
import br.com.vega.armazem.adapter.out.persistence.repository.CompartimentoJpaRepository;
import br.com.vega.armazem.domain.exception.RecursoNaoEncontradoException;
import br.com.vega.armazem.domain.model.Compartimento;
import br.com.vega.armazem.domain.port.out.CompartimentoPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA da porta de compartimentos.
 */
@Repository
public class CompartimentoJpaAdapter implements CompartimentoPort {

    private final CompartimentoJpaRepository repository;

    /**
     * Recebe o repositório Spring Data.
     *
     * @param repository repositório de compartimentos
     */
    public CompartimentoJpaAdapter(CompartimentoJpaRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Compartimento> buscarPorId(Long id) {
        return repository.findById(id).map(CompartimentoMapper::paraDominio);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Compartimento> buscarPorIdComBloqueio(Long id) {
        return repository.buscarParaAtualizacao(id).map(CompartimentoMapper::paraDominio);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Compartimento> listar() {
        return repository.findAll().stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .map(CompartimentoMapper::paraDominio)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Compartimento salvar(Compartimento compartimento) {
        CompartimentoEntity entity = repository.findById(compartimento.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Compartimento " + compartimento.getId() + " não foi encontrado."));
        CompartimentoMapper.copiar(compartimento, entity);
        return CompartimentoMapper.paraDominio(repository.save(entity));
    }
}
