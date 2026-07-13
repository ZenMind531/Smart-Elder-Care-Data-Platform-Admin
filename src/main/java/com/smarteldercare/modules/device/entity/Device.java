package com.smarteldercare.modules.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smarteldercare.common.BaseEntity;

@TableName("device")
public class Device extends BaseEntity {
    private String deviceCode;
    private String deviceName;
    private String deviceType;
    private Long elderlyId;
    private String status;
    private String remark;

    public String getDeviceCode() { return deviceCode; }
    public void setDeviceCode(String deviceCode) { this.deviceCode = deviceCode; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    public Long getElderlyId() { return elderlyId; }
    public void setElderlyId(Long elderlyId) { this.elderlyId = elderlyId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
