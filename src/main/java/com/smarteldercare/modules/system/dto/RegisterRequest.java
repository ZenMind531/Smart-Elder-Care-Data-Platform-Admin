package com.smarteldercare.modules.system.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {
    private String username;
    private String password;
    private String realName;
    private String phoneNumber;

}