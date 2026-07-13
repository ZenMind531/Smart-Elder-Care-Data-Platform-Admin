package com.smarteldercare.modules.system.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResult {
    private String token;        // ① JWT token，通行证
    private UserInfo userInfo;   // ② 嵌套的用户信息

    @Setter
    @Getter
    public static class UserInfo {   // ③ 内部类：只挑 4 个字段返回
        private Long id;             // 用户ID
        private String username;     // 用户名
        private String realName;     // 真实姓名
        private String roleName;     //角色名（管理员/医生）

    }

}