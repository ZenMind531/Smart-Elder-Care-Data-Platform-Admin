package com.smarteldercare.modules.system.controller;

import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.modules.system.dto.LoginRequest;
import com.smarteldercare.modules.system.dto.RegisterRequest;
import com.smarteldercare.modules.system.dto.UpdatePasswordRequest;
import com.smarteldercare.modules.system.service.UserService;
import com.smarteldercare.modules.system.vo.LoginResult;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ApiResponse<LoginResult> login(@RequestBody LoginRequest request) {
        LoginResult result = userService.login(
                request.getUsername(),
                request.getPassword()
        );
        return ApiResponse.success(result);
    }

    @GetMapping("/me")
    public ApiResponse<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof Long)) {
            return ApiResponse.error(401, "请先登录");
        }
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(userService.getById(userId));
    }

    @PutMapping("/password")
    public ApiResponse<?> updatePassword(@RequestBody UpdatePasswordRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof Long)) {
            return ApiResponse.error(401, "请先登录");
        }
        Long userId = (Long) auth.getPrincipal();
        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            return ApiResponse.error(400, "两次密码不一致");
        }
        userService.updatePassword(userId, req.getOldPassword(), req.getNewPassword());
        return ApiResponse.success();
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout() {
        return ApiResponse.success();
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ApiResponse.success();
    }
}
