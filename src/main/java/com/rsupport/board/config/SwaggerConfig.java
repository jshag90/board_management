package com.rsupport.board.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version("v1.0") //버전
                .title("Board Management API") //이름
                .description("알서포트 백엔드 개발자 과제 전형 공지사항 관리 REST API 구현"); //설명
        return new OpenAPI()
                .info(info);
    }

}
