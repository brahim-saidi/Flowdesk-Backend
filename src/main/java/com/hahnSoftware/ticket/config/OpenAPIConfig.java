package com.hahnSoftware.ticket.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;

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
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("tickets")
                .pathsToMatch("/api/**")
                .build();
    }
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Ticket Management System API")
                        .version("1.0")
                        .description("API Documentation for Ticket Management System")
                        .license(new License().name("Hahn Software")));
    }
}

