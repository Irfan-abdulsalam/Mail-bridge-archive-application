package com.Mail_Bridge_Archive.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mail Bridge Archive API Documentation")
                        .version("1.0.0")
                        .description("API documentation for the Mail Bridge Archive application")
                        .contact(new Contact()
                                .name("Irfan abdulsalam")
                                .url("https://www.linkedin.com/in/irfan-abdulsalam/")
                                .email("irfan.abdulsalam.dev@gmail.com")));
    }
}
