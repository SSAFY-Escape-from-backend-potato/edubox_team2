package com.backend_potato.edubox_team2.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info().title("EduBox API 명세서")
                .description("<h3>EduBox API Reference for Developers</h3>Swagger를 이용한 API<br>")
                .version("v1")
                .contact(new io.swagger.v3.oas.models.info.Contact().name("KSH")
                        .email("spancer1@naver.com")
                        .url("https://www.naver.com"));

        return new OpenAPI().components(new Components())
                .info(info);
    }
}
