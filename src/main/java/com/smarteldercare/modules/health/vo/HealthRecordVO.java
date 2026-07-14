package com.smarteldercare.modules.health.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HealthRecordVO {

    private Long id;

    private Long elderlyId;

    private String elderlyName;

    private Integer systolicPressure;

    private Integer diastolicPressure;

    private BigDecimal bloodSugar;

    private Integer heartRate;

    private BigDecimal temperature;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordTime;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
