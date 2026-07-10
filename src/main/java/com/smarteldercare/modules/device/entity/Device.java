package com.smarteldercare.modules.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smarteldercare.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("device")
public class Device extends BaseEntity {

    private String deviceCode;

    private String deviceName;

    private String deviceType;

    private Long elderlyId;

    private String status;

    private String remark;
}
