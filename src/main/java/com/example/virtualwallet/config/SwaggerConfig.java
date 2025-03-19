package com.example.virtualwallet.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@OpenAPIDefinition(
        info = @Info (
                contact = @Contact(
                        name = "Georgi Benchev and Ivan Ivanov",
                        email = "gega4321@gmail.com ; ivanovivanbusiness@gmail.com"
                ),
                description = "This is the **MoneyMe Virtual Wallet API**, a secure and efficient digital wallet " +
                        "solution developed using **Spring Boot**. The application allows users to:\n" +
                        "- **Register and authenticate** using JWT authentication.\n" +
                        "- **Add and manage credit/debit cards** securely.\n" +
                        "- **Deposit money** into their virtual wallet from a linked bank card.\n" +
                        "- **Send money** to users using phone numbers, usernames, or emails.\n" +
                        "- **View transaction history**, filterable by date, recipient, sender, etc.\n" +
                        "- **Manage multiple wallets**, one for each supported currency.\n" +
                        "- **Admin features**: Manage users, block/unblock accounts, and monitor transactions.\n" +
                        "- **Security features**: Identity verification and email verification.\n\n" +
                        "The API follows **RESTful principles** and provides a structured, scalable financial solution.",
                title = "MoneyMe - Virtual Wallet API",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Heroku Deployment",
                        url = "https://your-heroku-app.herokuapp.com"
                )
        }
)
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new io.swagger.v3.oas.models.security.SecurityScheme()
                                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}