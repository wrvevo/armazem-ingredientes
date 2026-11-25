package br.com.vega.armazem.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Descreve a API no Swagger e o esquema Bearer usado pelos endpoints protegidos.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Monta o documento OpenAPI com o título do desafio e a segurança JWT.
     *
     * @return documento publicado em /v3/api-docs
     */
    @Bean
    public OpenAPI armazemOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Armazém de Ingredientes")
                        .description("API do desafio técnico Vega para cadastro, movimentação e consulta de ingredientes. "
                                + "Autentique em POST /auth/login e envie o token no cabeçalho Authorization.")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                .components(new Components().addSecuritySchemes("bearer-jwt",
                        new SecurityScheme()
                                .name("bearer-jwt")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
