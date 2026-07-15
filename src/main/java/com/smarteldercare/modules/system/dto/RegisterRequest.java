package com.smarteldercare.modules.system.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;



@Getter
public class RegisterRequest {
    @Size(min = 4, max = 20, message = "用户名长度为4-20位")
    private String username;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).{8,20}$", message = "密码必须包含字母和数字，长度8-20位")
    private String password;

    @Size(min = 1, max = 50, message = "姓名不能为空")
    private String realName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确（需要11位手机号）")
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