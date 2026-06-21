package com.exercise.web.config;

import com.exercise.campus.interceptor.UserLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 用户端 WebMvc 配置：注册 studentToken 拦截器
 * 放行：登录 / 注册 / 文档 / 静态资源 / OPTIONS
 */
@Configuration
public class WebWebConfig implements WebMvcConfigurer {

    @Autowired
    private UserLoginInterceptor userLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration reg = registry.addInterceptor(userLoginInterceptor);
        reg.addPathPatterns("/**");
        reg.excludePathPatterns(
                "/userInfo/login",
                "/userInfo/register",
                "/userInfo/logout",
                "/doc.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/webjars/**",
                "/favicon.ico",
                "/error"
        );
    }
}