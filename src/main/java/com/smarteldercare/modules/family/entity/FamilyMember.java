package com.smarteldercare.modules.family.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smarteldercare.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("family_member")
public class FamilyMember extends BaseEntity {

    private String phone;

    private String password;

    private String name;

    private String status;

}