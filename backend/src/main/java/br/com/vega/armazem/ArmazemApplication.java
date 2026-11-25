package br.com.vega.armazem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

/**
 * Ponto de entrada da API do armazém de ingredientes.
 */
@SpringBootApplication
public class ArmazemApplication {

    /**
     * Prepara a pasta de logs e inicia o contexto do Spring Boot.
     *
     * @param args argumentos de linha de comando recebidos pela JVM
     */
    public static void main(String[] args) {
        prepararDiretorioDeLogs();
        SpringApplication.run(ArmazemApplication.class, args);
    }

    /**
     * Garante que o diretório usado pelo Log4j exista antes da primeira escrita.
     */
    private static void prepararDiretorioDeLogs() {
        String caminho = System.getProperty("LOG_PATH");
        if (caminho == null || caminho.isBlank()) {
            caminho = System.getenv().getOrDefault("LOG_PATH", "logs");
        }
        new File(caminho).mkdirs();
    }
}
