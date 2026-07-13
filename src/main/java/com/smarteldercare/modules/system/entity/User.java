package com.smarteldercare.modules.system.entity;

import com.smarteldercare.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User extends BaseEntity {
    private String username;
    private String password;
    private String realName;
    private String phoneNumber;
    private Long roleId;
    private String status;

    // 无参构造
    public User() {
    }


}
