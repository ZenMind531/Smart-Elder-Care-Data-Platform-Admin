package com.smarteldercare.modules.device.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeviceBindDTO {

    @NotNull(message = "老人ID不能为空")
    private Long elderlyId;
}
