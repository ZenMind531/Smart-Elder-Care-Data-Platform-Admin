package com.smarteldercare.modules.device.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceStatusDTO {

    @NotBlank(message = "不能为空")
    private String status;
}
