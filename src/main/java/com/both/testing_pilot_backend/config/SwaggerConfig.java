package com.both.testing_pilot_backend.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Test pilot")
                        .version("1.0")
                        .description("A modern, comprehensive API testing platform for validating REST API endpoints with scenario-based testing and monitoring.")
                        .contact(new Contact()
                                .name("API Support Team")
                                .email("yumateb@gmail.com")
                                .url("mailto:yumateb@gmail.com")))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8080").description("Local Development"),
                        new Server().url("https://api.testpilot.kshrd.app").description("Production")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("API Testing Platform Documentation")
                        .url("https://api.testpilot.kshrd.app/docs"));
    }
}