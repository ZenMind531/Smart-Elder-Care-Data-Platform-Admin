package com.smarteldercare.modules.device.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DeviceDTO {

    @NotBlank(message = "设备编号不能为空")
    private String deviceCode;

    @NotBlank(message = "设备名称不能为空")
    private String deviceName;

    @NotBlank(message = "设备类型不能为空")
    @Pattern(regexp = "watch|bp_meter|glucometer", message = "设备类型必须是 watch、bp_meter 或 glucometer")
    private String deviceType;

    @Pattern(regexp = "normal|abnormal|disabled", message = "状态必须是 normal、abnormal 或 disabled")
    private String status;

    private String remark;
}
