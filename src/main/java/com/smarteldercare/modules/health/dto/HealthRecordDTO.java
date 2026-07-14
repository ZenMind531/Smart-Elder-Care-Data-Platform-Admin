package com.smarteldercare.modules.health.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HealthRecordDTO {

    private Long elderlyId;

    private String elderlyName;

    @Min(value = 0, message = "不能小于0")
    @Max(value = 300, message = "不能大于300")
    private Integer systolicPressure;

    @Min(value = 0, message = "不能小于0")
    @Max(value = 200, message = "不能大于200")
    private Integer diastolicPressure;

    @DecimalMin(value = "0.00", message = "不能小于0")
    @DecimalMax(value = "50.00", message = "不能大于50")
    private BigDecimal bloodSugar;

    @Min(value = 0, message = "不能小于0")
    @Max(value = 250, message = "不能大于250")
    private Integer heartRate;

    @DecimalMin(value = "30.0", message = "不能小于30.0")
    @DecimalMax(value = "45.0", message = "不能大于45.0")
    private BigDecimal temperature;

    @NotNull(message = "不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordTime;

    private String remark;
}
