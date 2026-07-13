package com.smarteldercare.modules.appointment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AppointmentStatusDTO {

    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "pending|confirmed|completed|cancelled",
             message = "状态必须是 pending/confirmed/completed/cancelled")
    private String status;

    private String cancelReason;
}
