package com.lihao.CoffeeOrderSystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置类
 * 解决前端5173端口访问后端8080端口的跨域问题
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许所有接口跨域访问
        registry.addMapping("/**")
                // 允许所有前端域名访问（开发环境用）
                .allowedOriginPatterns("*")
                // 允许的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许所有请求头
                .allowedHeaders("*")
                // 允许携带凭证（如Cookie）
                .allowCredentials(true)
                // 预检请求的有效期，单位秒
                .maxAge(3600);
    }
}