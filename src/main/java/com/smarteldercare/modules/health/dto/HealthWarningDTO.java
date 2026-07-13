package com.smarteldercare.modules.health.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HealthWarningDTO {

    @NotNull(message = "不能为空")
    private Long elderlyId;

    @NotBlank(message = "不能为空")
    @Pattern(regexp = "blood_pressure|blood_sugar|heart_rate|temperature", message = "预警类型不正确")
    private String warningType;

    @NotBlank(message = "不能为空")
    @Pattern(regexp = "low|medium|high", message = "必须是 low、medium 或 high")
    private String warningLevel;

    @NotBlank(message = "不能为空")
    private String warningContent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime warningTime;
}
