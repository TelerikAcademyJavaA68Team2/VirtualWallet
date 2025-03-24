package com.example.virtualwallet.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
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
                description = """
                        This is the **MoneyMe Virtual Wallet API**, a secure and efficient digital wallet \
                        solution developed using **Spring Boot**. The application allows users to:
                        - **Register and authenticate** using JWT authentication.
                        - **Add and manage credit/debit cards** securely.
                        - **Deposit money** into their virtual wallet from a linked bank card.
                        - **Send money** to users using phone numbers, usernames, or emails.
                        - **View transaction history**, filterable by date, recipient, sender, etc.
                        - **Manage multiple wallets**, one for each supported currency.
                        - **Admin features**: Manage users, block/unblock accounts, and monitor transactions.
                        
                        The API follows **RESTful principles** and provides a structured, scalable financial solution.""",
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
                        url = "https://money-me-84fb9ba46b45.herokuapp.com/"
                )
        },
        externalDocs = @ExternalDocumentation(
                description = "📘 GitHub Repository",
                url = "https://github.com/TelerikAcademyJavaA68Team2/VirtualWallet"
        )
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