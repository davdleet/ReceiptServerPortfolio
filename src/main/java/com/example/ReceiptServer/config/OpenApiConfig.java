package com.example.ReceiptServer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI receiptServerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Receipt Server API")
                        .description("API for receipt management")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Developer")
                                .email("contact@example.com")));
    }
}