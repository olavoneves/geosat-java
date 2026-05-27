package br.com.geosat.server.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI geosatOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GeoSat API")
                        .description("API REST de monitoramento agrícola satelital — GeoSat | FIAP Global Solution 2026/1")
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("UUID")
                                .description("Token de acesso obtido via POST /auth/login")));
    }
}
