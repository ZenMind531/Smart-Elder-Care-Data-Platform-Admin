package com.smarteldercare.modules.family.vo;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
public class ReservationVO {
    private Long id;
    private Long elderlyId;
    private String elderlyName;
    private String serviceType;
    private LocalDate serviceDate;
    private String serviceTime;
    private String remark;
    private String status;
    private LocalDateTime createTime;
}