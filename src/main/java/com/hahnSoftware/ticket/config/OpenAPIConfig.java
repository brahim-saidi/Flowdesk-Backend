package com.hahnSoftware.ticket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Ticket Management System API",
                version = "1.0",
                description = "API Documentation for Ticket Management System"
        )
)
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String schemeName = "bearer-jwt";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components()
                        .addSecuritySchemes(schemeName,
                                new SecurityScheme()
                                        .name(schemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Ticket Management System API")
                        .version("1.0")
                        .description("API Documentation for Ticket Management System. Use POST /api/auth/login to obtain a JWT, then Authorize with: Bearer <token>")
                        .license(new License().name("Hahn Software")));
    }
}
