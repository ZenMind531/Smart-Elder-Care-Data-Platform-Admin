package com.smarteldercare.modules.report.entity;
import java.time.LocalDateTime;
import com.smarteldercare.common.BaseEntity;

        public class AssessmentReport extends BaseEntity
        {
            private Long id;


            public String getReportTitle() {
                return reportTitle;
            }

            public void setReportTitle(String reportTitle) {
                this.reportTitle = reportTitle;
            }

            public String getRiskLevel() {
                return riskLevel;
            }

            public void setRiskLevel(String riskLevel) {
                this.riskLevel = riskLevel;
            }

            public String getSummary() {
                return summary;
            }

            public void setSummary(String summary) {
                this.summary = summary;
            }

            public String getSuggestion() {
                return suggestion;
            }

            public void setSuggestion(String suggestion) {
                this.suggestion = suggestion;
            }

            private String reportTitle;

            public LocalDateTime getReportTime() {
                return reportTime;
            }

            public void setReportTime(LocalDateTime reportTime) {
                this.reportTime = reportTime;
            }

            private String riskLevel;
            private String summary;


            @Override
            public Long getId() {
                return id;
            }

            @Override
            public void setId(Long id) {
                this.id = id;
            }

            private String suggestion;
            private LocalDateTime reportTime;
        }

