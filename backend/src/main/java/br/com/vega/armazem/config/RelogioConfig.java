package br.com.vega.armazem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/**
 * Disponibiliza o relógio oficial das regras que dependem do dia corrente.
 */
@Configuration
public class RelogioConfig {

    /**
     * Cria o relógio no fuso de São Paulo.
     *
     * @return relógio do sistema
     */
    @Bean
    public Clock relogio() {
        return Clock.system(ZoneId.of("America/Sao_Paulo"));
    }
}
