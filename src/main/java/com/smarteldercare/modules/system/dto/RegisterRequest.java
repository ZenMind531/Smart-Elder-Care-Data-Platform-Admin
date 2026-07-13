package com.smarteldercare.modules.system.dto;

import lombok.Getter;
import lombok.Setter;



@Getter
public class RegisterRequest {
    private String username;
    private String password;
    private String realName;
    private String phoneNumber;
    private Long RoleId;

    private String gender;//性别

    private String department;//科室
    private String title;//职称

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRoleId(Long roleId) {
        RoleId = roleId;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}