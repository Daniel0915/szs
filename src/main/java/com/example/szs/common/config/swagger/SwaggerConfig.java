package com.example.szs.common.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@OpenAPIDefinition(
        info = @Info(
                title = "goods 서버 API 명세서",
                description = "goods 서버에서 REST하게 작성된 API 명세서",
                version = "v1"
        )
)
@Configuration
public class SwaggerConfig {
    @Bean
    @Profile("!Prod")
    public OpenAPI openAPI() {
        String jwtSchemeName = "Bearer 토큰 입력";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("Bearer")
                        .bearerFormat("Bearer"));

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
