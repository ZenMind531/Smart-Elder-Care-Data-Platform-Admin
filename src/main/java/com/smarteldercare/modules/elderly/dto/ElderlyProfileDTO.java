package com.smarteldercare.modules.elderly.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ElderlyProfileDTO {

    @NotBlank(message = "不能为空")
    private String elderlyName;

    @NotBlank(message = "不能为空")
    @Pattern(regexp = "male|female|unknown", message = "必须是 male、female 或 unknown")
    private String gender;

    @NotNull(message = "不能为空")
    @Min(value = 0, message = "不能小于0")
    @Max(value = 130, message = "不能大于130")
    private Integer age;

    @NotBlank(message = "身份证号不能为空")
    private String idCard;

    private String phoneNumber;

    private String address;

    private String emergencyContact;

    private String emergencyPhone;

    private String medicalHistory;

    private String allergyHistory;

    @Pattern(regexp = "low|medium|high", message = "必须是 low、medium 或 high")
    private String riskLevel;
}
