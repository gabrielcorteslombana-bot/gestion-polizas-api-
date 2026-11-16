package com.seguros.polizas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Plataforma de Gestión de Pólizas de Arrendamiento")
                        .version("1.0.0")
                        .description("API REST para la administración de pólizas individuales y colectivas, renovación con ajuste de IPC, cancelación en cascada y sincronización con capa media WebLogic del CORE.")
                        .contact(new Contact()
                                .name("Equipo de Arquitectura e Ingeniería de TI")
                                .email("arquitectura@seguros.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
