package com.smarteldercare.modules.appointment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDTO {

    @NotNull(message = "老人ID不能为空")
    private Long elderlyId;

    @NotBlank(message = "服务类型不能为空")
    @Pattern(regexp = "health_check|home_care|rehabilitation|consultation|other",
             message = "服务类型必须是 health_check/home_care/rehabilitation/consultation/other")
    private String serviceType;

    @NotNull(message = "预约时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentTime;

    private String doctorName;

    private String description;
}
