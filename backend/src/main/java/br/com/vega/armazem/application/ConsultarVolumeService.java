package br.com.vega.armazem.application;

import br.com.vega.armazem.domain.model.TipoIngrediente;
import br.com.vega.armazem.domain.model.VolumePorTipo;
import br.com.vega.armazem.domain.port.in.ConsultarVolumeUseCase;
import br.com.vega.armazem.domain.port.out.IngredientePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * Consolida o estoque total de cada tipo de ingrediente.
 */
@Service
public class ConsultarVolumeService implements ConsultarVolumeUseCase {

    private final IngredientePort ingredientePort;

    /**
     * Recebe a porta que soma os saldos.
     *
     * @param ingredientePort porta dos ingredientes
     */
    public ConsultarVolumeService(IngredientePort ingredientePort) {
        this.ingredientePort = ingredientePort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<VolumePorTipo> listar() {
        Map<TipoIngrediente, BigDecimal> totais = ingredientePort.somarPorTipo();
        return List.of(TipoIngrediente.LIQUIDO, TipoIngrediente.REFRIGERADO, TipoIngrediente.SECO).stream()
                .map(tipo -> new VolumePorTipo(tipo, escala(totais.get(tipo))))
                .toList();
    }

    /**
     * Normaliza um total ausente para zero com três casas decimais.
     *
     * @param valor soma vinda do banco, ou nula
     * @return quantidade com escala 3
     */
    private BigDecimal escala(BigDecimal valor) {
        if (valor == null) {
            return new BigDecimal("0.000");
        }
        return valor.setScale(3, RoundingMode.HALF_UP);
    }
}
