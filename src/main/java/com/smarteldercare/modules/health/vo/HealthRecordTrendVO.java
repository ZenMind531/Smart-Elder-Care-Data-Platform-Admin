package com.smarteldercare.modules.health.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HealthRecordTrendVO {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordTime;

    private Integer systolicPressure;

    private Integer diastolicPressure;

    private BigDecimal bloodSugar;

    private Integer heartRate;

    private BigDecimal temperature;
}
