package com.mobi7.positionapi.config;


import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("POI and POSITION Tracking System")
                        .description("This project aims to provide a backend API for querying the duration that vehicles have spent within each Point of Interest (POI)")
                        .version("1.0")
                ).externalDocs(
                        new ExternalDocumentation()
                                .description("Ronaldo Caldas")
                                .url("https://github.com/ronaldocaldas/position-api"));
    }
}
