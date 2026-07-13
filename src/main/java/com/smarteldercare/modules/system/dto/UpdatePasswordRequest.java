package com.smarteldercare.modules.system.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdatePasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

}
