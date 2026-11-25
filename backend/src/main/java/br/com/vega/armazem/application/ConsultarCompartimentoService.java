package br.com.vega.armazem.application;

import br.com.vega.armazem.domain.exception.RegraNegocioException;
import br.com.vega.armazem.domain.model.Compartimento;
import br.com.vega.armazem.domain.model.Disponibilidade;
import br.com.vega.armazem.domain.model.RegraArmazenamento;
import br.com.vega.armazem.domain.model.TipoIngrediente;
import br.com.vega.armazem.domain.port.in.ConsultarCompartimentoUseCase;
import br.com.vega.armazem.domain.port.out.CompartimentoPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * Calcula, em uma única leitura dos cinco compartimentos, o espaço livre e o estoque vendável.
 */
@Service
public class ConsultarCompartimentoService implements ConsultarCompartimentoUseCase {

    private final CompartimentoPort compartimentoPort;
    private final Clock relogio;

    /**
     * Recebe a porta de compartimentos e o relógio da regra diária.
     *
     * @param compartimentoPort porta dos compartimentos
     * @param relogio relógio do sistema
     */
    public ConsultarCompartimentoService(CompartimentoPort compartimentoPort, Clock relogio) {
        this.compartimentoPort = compartimentoPort;
        this.relogio = relogio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Compartimento> listar() {
        return compartimentoPort.listar();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Disponibilidade> listarDisponiveis(BigDecimal quantidade, TipoIngrediente tipo) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("A quantidade deve ser maior que zero.");
        }
        if (tipo == null) {
            throw new RegraNegocioException("O tipo do ingrediente é obrigatório.");
        }
        LocalDate hoje = LocalDate.now(relogio);
        return compartimentoPort.listar().stream()
                .map(compartimento -> paraDisponibilidade(compartimento, tipo, hoje))
                .filter(disponibilidade -> disponibilidade.getEspacoLivre().compareTo(quantidade) >= 0)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Disponibilidade> listarDisponiveisParaVenda(TipoIngrediente tipo) {
        if (tipo == null) {
            throw new RegraNegocioException("O tipo do ingrediente é obrigatório.");
        }
        LocalDate hoje = LocalDate.now(relogio);
        return compartimentoPort.listar().stream()
                .filter(compartimento -> compartimento.possuiEstoque() && compartimento.getTipoAtual() == tipo)
                .map(compartimento -> paraDisponibilidade(compartimento, tipo, hoje))
                .toList();
    }

    /**
     * Projeta o compartimento com a capacidade e o espaço livre do tipo consultado.
     *
     * @param compartimento compartimento lido
     * @param tipo tipo usado no cálculo
     * @param hoje data corrente
     * @return disponibilidade calculada
     */
    private Disponibilidade paraDisponibilidade(Compartimento compartimento, TipoIngrediente tipo, LocalDate hoje) {
        return new Disponibilidade(
                compartimento.getId(),
                compartimento.getCodigo(),
                compartimento.getTipoAtual(),
                compartimento.getQuantidadeAtual(),
                tipo.getCapacidadeMaxima(),
                RegraArmazenamento.calcularEspacoLivre(compartimento, tipo, hoje),
                tipo.getUnidade());
    }
}
