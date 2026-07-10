package com.smarteldercare.modules.device.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceDTO {

    @NotBlank(message = "不能为空")
    private String deviceCode;

    @NotBlank(message = "不能为空")
    private String deviceName;

    @NotBlank(message = "不能为空")
    private String deviceType;

    private Long elderlyId;

    private String status;

    private String remark;
}
