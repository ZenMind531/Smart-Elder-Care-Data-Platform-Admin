package com.smarteldercare.modules.family.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smarteldercare.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("service_reservation")
public class ServiceReservation extends BaseEntity {

    private Long familyMemberId;

    private Long elderlyId;

    private String serviceType;

    private LocalDate serviceDate;

    private String serviceTime;

    private String remark;

    private String status;
}