package com.smarteldercare.modules.system.controller;

import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.modules.system.dto.LoginRequest;
import com.smarteldercare.modules.system.dto.RegisterRequest;
import com.smarteldercare.modules.system.dto.UpdatePasswordRequest;
import com.smarteldercare.modules.system.service.UserService;
import com.smarteldercare.modules.system.vo.LoginResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // ① 登录
    @PostMapping("/login")
    public ApiResponse<LoginResult> login(@RequestBody LoginRequest
                                                  request) {
        LoginResult result = userService.login(
                request.getUsername(),
                request.getPassword()
        );
        return ApiResponse.success(result);
    }

    // ② 获取当前用户
    @GetMapping("/me")
    public ApiResponse<?> me(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        return ApiResponse.success(userService.getById(userId));
    }
    //改密码
    @PutMapping("/password")
    public ApiResponse<?> updatePassword(HttpServletRequest request,
                                         @RequestBody
                                         UpdatePasswordRequest req) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            return ApiResponse.error(400, "两次密码不一致");
        }
        userService.updatePassword(userId, req.getOldPassword(),
                req.getNewPassword());
        return ApiResponse.success();
    }

    // ③ 退出登录
    @PostMapping("/logout")
    public ApiResponse<?> logout() {
        return ApiResponse.success();
    }
    // ④ 注册
    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return ApiResponse.success();
    }
}