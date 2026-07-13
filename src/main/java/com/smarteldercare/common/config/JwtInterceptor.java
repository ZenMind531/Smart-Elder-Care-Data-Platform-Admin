package com.smarteldercare.common.config;

import com.smarteldercare.common.utils.JwtUtil;
import com.smarteldercare.modules.system.entity.Role;
import com.smarteldercare.modules.system.entity.User;
import com.smarteldercare.modules.system.mapper.RoleMapper;
import com.smarteldercare.modules.system.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        System.out.println("DEBUG: Interceptor called - uri=" +
                request.getRequestURI());
        // 从请求头拿 token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            String uri = request.getRequestURI();
            // 只有这三个不需要 token
            if (uri.startsWith("/api/auth/login") ||
                    uri.startsWith("/api/auth/logout") ||
                    uri.startsWith("/api/auth/register")) {
                return true;
            }
            // 其他全部拦
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");

            response.getWriter().write("{\"code\":401,\"message\":\"请先登录\"}");
            return false;
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
            User user = userMapper.selectById(userId);
            if (user != null && user.getRoleId() != null) {
                Role role = roleMapper.selectById(user.getRoleId());
                if (role != null) {
                    request.setAttribute("roleCode", role.getRoleCode());
                }
            }
            System.out.println("拿完token");
        } catch (Exception e) {
            // token 无效或过期
            e.printStackTrace();  // ← 加这一行
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"to ken无效或已过期\"}");
            return false;  // 拦截
        }

        String uri = request.getRequestURI();
        String roleCode = (String) request.getAttribute("roleCode");

        // /api/roles 全部操作仅管理员
        if (uri.startsWith("/api/roles") && !"admin".equals(roleCode)) {
            response.setStatus(403);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":403,\"message\":\"无权限，仅管 理员可操作\"}");
            return false;
        }

        // /api/users 的增删改仅管理员
        if (uri.startsWith("/api/users") && !"admin".equals(roleCode)) {
            String method = request.getMethod();

                response.setStatus(403);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"message\":\"无权限， 仅管理员可操作\"}");
                return false;

        }

        // /api/doctors 的增删改仅管理员
        if (uri.startsWith("/api/doctors") && !"admin".equals(roleCode)) {
            String method = request.getMethod();

                response.setStatus(403);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"message\":\"无权限， 仅管理员可操作\"}");
                return false;

        }
        System.out.println("DEBUG: roleCode=" + roleCode + " method=" + request.getMethod() + " uri=" + uri);
        return true;  // 放行
    }
}
