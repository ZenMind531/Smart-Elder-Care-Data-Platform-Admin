package com.smarteldercare.modules.family.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginVO {
    private String token;
    private Long id;
    private String name;
}