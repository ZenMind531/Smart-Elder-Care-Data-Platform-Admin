package com.smarteldercare.modules.appointment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smarteldercare.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("appointment")
public class Appointment extends BaseEntity {

    private Long elderlyId;

    private String serviceType;

    private LocalDateTime appointmentTime;

    private String doctorName;

    private String status;

    private String description;

    private String cancelReason;
}
