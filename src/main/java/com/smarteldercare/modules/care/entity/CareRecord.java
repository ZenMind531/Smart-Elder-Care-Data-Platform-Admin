package com.smarteldercare.modules.care.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smarteldercare.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("care_record")
public class CareRecord extends BaseEntity {

    private Long elderlyId;

    private String caregiver;

    private String careType;

    private String careContent;

    private LocalDateTime careTime;

    private String remark;
}
