package com.smarteldercare.modules.elderly.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smarteldercare.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("elderly_profile")
public class ElderlyProfile extends BaseEntity {

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
}
