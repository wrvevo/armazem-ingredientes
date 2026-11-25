package br.com.vega.armazem.domain.port.in;

import br.com.vega.armazem.domain.model.VolumePorTipo;

import java.util.List;

/**
 * Caso de uso do volume total por tipo de ingrediente.
 */
public interface ConsultarVolumeUseCase {

    /**
     * Soma o saldo de secos, líquidos e refrigerados.
     *
     * @return os três tipos, mesmo quando o total é zero
     */
    List<VolumePorTipo> listar();
}
