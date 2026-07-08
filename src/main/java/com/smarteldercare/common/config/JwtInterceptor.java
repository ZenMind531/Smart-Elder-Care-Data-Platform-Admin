package com.smarteldercare.common.config;

import com.smarteldercare.common.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 从请求头拿 token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return true;  // 没 token，放行
        }

        // 截掉 "Bearer " 拿到纯 token
        String token = authHeader.substring(7);

        try {
            // 解析 token，取出用户信息
            Long userId = JwtUtil.getUserId(token);
            String username = JwtUtil.getUsername(token);

            // 存到 request 里，Controller 可以取
            request.setAttribute("userId", userId);
            request.setAttribute("username", username);

        } catch (Exception e) {
            // token 无效或过期
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"to ken无效或已过期\"}");
            return false;  // 拦截
        }

        return true;  // 放行
    }
}
