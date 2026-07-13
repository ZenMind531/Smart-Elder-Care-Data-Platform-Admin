package com.smarteldercare.modules.family.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Setter
@Getter
public class ReservationRequest {
    private Long elderlyId;
    private String serviceType;
    private LocalDate serviceDate;
    private String serviceTime;
    private String remark;
}