package br.com.product.micro.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("/")
                ))
                .info(new Info()
                        .title("Product Microservice")
                        .version("1.0.0")
                        .description("API for performing actions related to product")
                        .contact(new Contact()
                                .name("Jobson Oliveira")
                                .email("jobsondeveloper@gmail.com")
                                .url("http://jobsondeveloper.vercel.app")
                        )
                );
    }
}
