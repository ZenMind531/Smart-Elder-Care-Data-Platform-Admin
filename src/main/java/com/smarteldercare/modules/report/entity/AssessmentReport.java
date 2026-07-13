package com.smarteldercare.modules.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smarteldercare.common.BaseEntity;
import java.time.LocalDateTime;

@TableName("assessment_report")
public class AssessmentReport extends BaseEntity {
    private Long elderlyId;
    private String reportTitle;
    private String riskLevel;
    private String summary;
    private String suggestion;
    private LocalDateTime reportTime;

    public Long getElderlyId() { return elderlyId; }
    public void setElderlyId(Long elderlyId) { this.elderlyId = elderlyId; }
    public String getReportTitle() { return reportTitle; }
    public void setReportTitle(String reportTitle) { this.reportTitle = reportTitle; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    public LocalDateTime getReportTime() { return reportTime; }
    public void setReportTime(LocalDateTime reportTime) { this.reportTime = reportTime; }
}
