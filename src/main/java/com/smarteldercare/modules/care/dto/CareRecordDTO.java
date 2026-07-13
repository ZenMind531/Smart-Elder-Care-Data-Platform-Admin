package com.smarteldercare.modules.care.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CareRecordDTO {

    @NotNull(message = "老人ID不能为空")
    private Long elderlyId;

    private String caregiver;

    @NotBlank(message = "护理类型不能为空")
    @Pattern(regexp = "medication|vital_signs|cleaning|feeding|exercise|other",
             message = "护理类型必须是 medication/vital_signs/cleaning/feeding/exercise/other")
    private String careType;

    @NotBlank(message = "护理内容不能为空")
    private String careContent;

    @NotNull(message = "护理时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime careTime;

    private String remark;
}
