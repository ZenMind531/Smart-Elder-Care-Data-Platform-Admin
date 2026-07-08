package com.smarteldercare.modules.elderly.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ElderlyProfileVO {

    private Long id;

    private String elderlyName;

    private String gender;

    private Integer age;

    private String idCard;

    private String phoneNumber;

    private String address;

    private String emergencyContact;

    private String emergencyPhone;

    private String medicalHistory;

    private String allergyHistory;

    private String riskLevel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
