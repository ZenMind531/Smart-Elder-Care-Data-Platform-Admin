package com.smarteldercare.modules.system.vo;

public class LoginResult {
    private String token;        // ① JWT token，通行证
    private UserInfo userInfo;   // ② 嵌套的用户信息

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public static class UserInfo {   // ③ 内部类：只挑 4 个字段返回
        private Long id;             // 用户ID
        private String username;     // 用户名
        private String realName;     // 真实姓名
        private String roleName;     //角色名（管理员/医生）

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }
    }

}