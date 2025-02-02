package org.wsp.mybookshelf.global;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 /api/** 요청에 대해 CORS 허용
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8080") // 프론트엔드 주소 (필요에 따라 변경)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true); // 쿠키 등을 사용할 경우 true로 설정
    }
}
