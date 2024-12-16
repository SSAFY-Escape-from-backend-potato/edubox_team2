package com.backend_potato.edubox_team2.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY) // API Key 타입으로 설정
                .in(SecurityScheme.In.HEADER)    // 헤더로 전달
                .name("access-token")            // 헤더 이름을 access-token으로 지정
                .description("Enter your JWT Access Token");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("AccessTokenAuth");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("AccessTokenAuth", securityScheme))
                .addSecurityItem(securityRequirement)
                .info(info);
    }
}
