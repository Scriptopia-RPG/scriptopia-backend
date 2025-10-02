package com.scriptopia.demo.config;

import com.scriptopia.demo.dto.auth.LoginRequest;
import io.swagger.v3.oas.models.examples.Example;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerExampleConfig {


    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;


    @Bean
    public OpenApiCustomizer customiseExamples() {
        return openApi -> {
            if (openApi.getPaths() == null) return;

            openApi.getPaths().forEach((path, item) -> {
                if (path.endsWith("/auth/login") && item.getPost() != null) {
                    var reqBody = item.getPost().getRequestBody();
                    if (reqBody == null) return;

                    var content = reqBody.getContent().get("application/json");
                    if (content == null) return;

                    // DTO 객체 그대로 넣기
                    content.addExamples("어드민 계정",
                            new Example().value(new LoginRequest(adminUsername, adminPassword, "1234")));

                    content.addExamples("일반 유저 계정",
                            new Example().value(new LoginRequest("userA@example.com", "userA!234", "1234")));
                }
            });
        };
    }
}
