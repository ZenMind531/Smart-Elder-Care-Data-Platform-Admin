package com.smarteldercare.modules.system.entity;

import com.smarteldercare.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Doctor extends BaseEntity {
    private String doctorName;
    private String gender;
    private String phoneNumber;
    private String department;
    private String title;
    private String status;

    public Doctor() {
    }

}