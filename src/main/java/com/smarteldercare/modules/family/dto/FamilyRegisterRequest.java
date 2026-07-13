package com.smarteldercare.modules.family.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FamilyRegisterRequest {
    private String phone;
    private String password;
    private String name;
}
