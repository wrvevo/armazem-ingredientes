package br.com.vega.armazem.adapter.out.persistence;

import br.com.vega.armazem.adapter.out.persistence.mapper.MovimentacaoMapper;
import br.com.vega.armazem.adapter.out.persistence.repository.MovimentacaoJpaRepository;
import br.com.vega.armazem.domain.model.Movimentacao;
import br.com.vega.armazem.domain.port.out.HistoricoPort;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Adaptador JPA da porta de histórico.
 */
@Repository
public class HistoricoJpaAdapter implements HistoricoPort {

    private final MovimentacaoJpaRepository repository;

    /**
     * Recebe o repositório Spring Data.
     *
     * @param repository repositório de movimentações
     */
    public HistoricoJpaAdapter(MovimentacaoJpaRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Movimentacao registrar(Movimentacao movimentacao) {
        return MovimentacaoMapper.paraDominio(repository.save(MovimentacaoMapper.paraEntidade(movimentacao)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Movimentacao> listar(String campo, String direcao) {
        Sort.Direction direction = Sort.Direction.fromString(direcao);
        String secundario = "compartimentoId".equals(campo) ? "dataHora" : "compartimentoId";
        Sort ordem = Sort.by(new Sort.Order(direction, campo), new Sort.Order(direction, secundario));
        return repository.findAll(ordem).stream().map(MovimentacaoMapper::paraDominio).toList();
    }
}
