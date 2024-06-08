package com.example.SouthernOceanSentinel_API.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI configuracaoOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("Southern Ocean Sentinel API")
                        .version("1.0")
                        .description("Southern Ocean Sentinel API")
                        .contact(new Contact().email("nextGen@gmail.com")
                                .name("Ricardo, Thabata, Yago, Michael")
                                .url("https:southernoceansentinel.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("url da licença")));
    }
}
