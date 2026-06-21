package com.exercise.admin.config;

import com.exercise.campus.interceptor.AdminLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 管理端 WebMvc 配置：注册 adminToken 拦截器
 * 放行路径：登录接口 / 文档 / 静态资源 / OPTIONS
 */
@Configuration
public class AdminWebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminLoginInterceptor adminLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration reg = registry.addInterceptor(adminLoginInterceptor);
        reg.addPathPatterns("/**");
        reg.excludePathPatterns(
                "/admin/login",
                "/admin/logout",
                "/doc.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/webjars/**",
                "/favicon.ico",
                "/error"
        );
    }
}