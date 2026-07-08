package com.smarteldercare.modules.health.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smarteldercare.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("health_record")
public class HealthRecord extends BaseEntity {

    private Long elderlyId;

    private Integer systolicPressure;

    private Integer diastolicPressure;

    private BigDecimal bloodSugar;

    private Integer heartRate;

    private BigDecimal temperature;

    private LocalDateTime recordTime;

    private String remark;


}
