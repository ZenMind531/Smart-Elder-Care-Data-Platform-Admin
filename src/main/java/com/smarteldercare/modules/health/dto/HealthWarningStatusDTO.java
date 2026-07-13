package com.smarteldercare.modules.health.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class HealthWarningStatusDTO {

    @NotBlank(message = "不能为空")
    @Pattern(regexp = "pending|processing|resolved", message = "必须是 pending、processing 或 resolved")
    private String status;

    private String handleResult;
}
