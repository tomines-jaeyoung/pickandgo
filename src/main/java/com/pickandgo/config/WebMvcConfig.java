package com.pickandgo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 업로드된 이미지 파일을 classpath가 아닌 실제 파일시스템 경로에서 서빙.
 *
 * Spring Boot 정적 리소스는 classpath 기반이라,
 * 앱 실행 후 동적으로 저장된 파일은 바로 서빙이 안 됨.
 * 이 설정으로 /uploads/** 요청을 실제 uploads 폴더에서 읽어줌.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${pickandgo.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        String location = uploadPath.toUri().toString();
        registry
            .addResourceHandler("/uploads/**")
            .addResourceLocations(location);
    }
}
