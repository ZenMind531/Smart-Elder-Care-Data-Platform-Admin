package com.smarteldercare.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new
                MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new
                PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }


    @Autowired
    private JwtInterceptor jwtInterceptor;
    @Bean
    public WebMvcConfigurer jwtConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(jwtInterceptor)
                        .addPathPatterns("/api/**")           // 拦截所有/api/ 开头的
                        .excludePathPatterns(
                                "/api/auth/login",            // 登录不用token
                                "/api/auth/logout"   ,         // 退出不用token
                                "/api/auth/register"
                        );
            }
        };
    }
}