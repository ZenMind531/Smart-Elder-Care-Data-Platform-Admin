package com.smarteldercare.modules.health.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smarteldercare.common.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("health_warning")
public class HealthWarning extends BaseEntity {

    private Long elderlyId;

    private Long healthRecordId;

    private Long retestRecordId;

    private String warningType;

    private String warningLevel;

    private String warningContent;

    private String status;

    private String handleResult;

    private LocalDateTime warningTime;

    private LocalDateTime handleTime;
}
