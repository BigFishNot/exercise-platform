package com.exercise.campus.interceptor;

import com.exercise.campus.component.LoginContextHolder;
import com.exercise.campus.component.LoginRedisComponent;
import com.exercise.campus.enums.ResponseCodeEnum;
import com.exercise.campus.enums.RoleTypeEnum;
import com.exercise.campus.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户端登录拦截器
 * 读 header studentToken，查 Redis 登录组件，校验 roleType=USER，写入登录上下文持有器
 */
@Component
public class UserLoginInterceptor implements HandlerInterceptor {

    public static final String HEADER_TOKEN = "studentToken";

    @Autowired
    private LoginRedisComponent loginRedisComponent;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader(HEADER_TOKEN);
        LoginRedisComponent.CacheValue v = loginRedisComponent.getUser(token);
        if (v == null) {
            throw new BusinessException(ResponseCodeEnum.UNAUTHORIZED);
        }
        if (!RoleTypeEnum.USER.getRoleType().equals(v.getRoleType())) {
            throw new BusinessException(ResponseCodeEnum.ROLE_NOT_MATCH);
        }
        LoginContextHolder.set(v.getUserId(), v.getRoleType(), v.getAccount(), v.getNickName());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        LoginContextHolder.clear();
    }
}