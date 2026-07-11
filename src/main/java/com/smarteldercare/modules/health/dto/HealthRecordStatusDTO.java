package com.smarteldercare.modules.health.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class HealthRecordStatusDTO {

    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "normal|abnormal", message = "状态必须是 normal 或 abnormal")
    private String status;
}
