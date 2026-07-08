package com.smarteldercare.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smarteldercare.modules.system.dto.RegisterRequest;
import com.smarteldercare.modules.system.entity.User;
import com.smarteldercare.modules.system.vo.LoginResult;

public interface UserService extends IService<User> {
    LoginResult login(String username, String password);
    void register(RegisterRequest request);
    void updatePassword(Long userId, String oldPassword, String
            newPassword);
}