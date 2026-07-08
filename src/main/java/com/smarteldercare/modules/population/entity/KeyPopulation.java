package com.smarteldercare.modules.population.entity;

import com.smarteldercare.common.BaseEntity;

public class KeyPopulation extends BaseEntity {
    private Long elderlyId;
    private String populationType;
    private String reason;
    private String followStatus;
    private String remark;

    public Long getElderlyId() { return elderlyId; }
    public void setElderlyId(Long elderlyId) { this.elderlyId = elderlyId; }
    public String getPopulationType() { return populationType; }
    public void setPopulationType(String populationType) { this.populationType = populationType; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getFollowStatus() { return followStatus; }
    public void setFollowStatus(String followStatus) { this.followStatus = followStatus; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
