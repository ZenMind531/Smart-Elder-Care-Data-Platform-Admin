package com.smarteldercare.modules.population.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smarteldercare.common.BaseEntity;

@TableName("key_population")
public class KeyPopulation extends BaseEntity {
    private Long elderlyId;

    @TableField(exist = false)
    private String elderlyName;
    private String populationType;
    private String reason;
    private String followStatus;
    private String remark;

    public Long getElderlyId() { return elderlyId; }
    public void setElderlyId(Long elderlyId) { this.elderlyId = elderlyId; }
    public String getElderlyName() { return elderlyName; }
    public void setElderlyName(String elderlyName) { this.elderlyName = elderlyName; }
    public String getPopulationType() { return populationType; }
    public void setPopulationType(String populationType) { this.populationType = populationType; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getFollowStatus() { return followStatus; }
    public void setFollowStatus(String followStatus) { this.followStatus = followStatus; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
