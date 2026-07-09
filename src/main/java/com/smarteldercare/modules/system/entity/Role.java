package com.smarteldercare.modules.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Role {
    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Role() {
    }

}