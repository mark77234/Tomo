package com.example.tomo.global.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TOMO API 문서")
                        .description("Firebase + JWT 기반 인증 시스템의 REST API 명세")
                        .version("1.0.0")
                );
    }
}

