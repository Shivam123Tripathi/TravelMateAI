package com.travelmateai.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger / OpenAPI Configuration.
 * Provides interactive API documentation at /swagger-ui.html
 * with JWT Bearer token authentication support.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("TravelMate AI - REST API")
                        .description("""
                                Production-ready REST API for TravelMate AI — a smart travel assistant platform.

                                ## Authentication
                                1. Register a new account using `/api/users/register`
                                2. Login using `/api/users/login` to get a JWT token
                                3. Click the **Authorize** button above and enter: `Bearer <your-token>`
                                4. All authenticated endpoints will now work

                                ## Roles
                                - **USER**: Can browse trips, book seats, manage profile
                                - **ADMIN**: Can manage trips, view reports and analytics
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("TravelMate AI Team")
                                .email("travelmateai@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token (without the 'Bearer ' prefix)")));
    }
}
