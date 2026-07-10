package com.smarteldercare.modules.device.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DeviceVO {

    private Long id;

    private String deviceCode;

    private String deviceName;

    private String deviceType;

    private Long elderlyId;

    private String elderlyName;

    private String status;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
