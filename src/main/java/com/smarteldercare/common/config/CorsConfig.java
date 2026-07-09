package com.smarteldercare.common.config;

import org.springframework.context.annotation.Bean;
import
        org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");   //允许任何前端地址
        config.setAllowCredentials(true);       //允许带 token
        config.addAllowedHeader("*");           //允许任何请求头
        config.addAllowedMethod("*");           //允许任何请求方式

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // 对所有接口生效
        return new CorsFilter(source);
    }
}