package com.smarteldercare.modules.family.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FamilyLoginRequest {
    private String phone;
    private String password;
}