package com.smarteldercare.modules.system.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smarteldercare.modules.system.entity.Role;
import com.smarteldercare.modules.system.mapper.RoleMapper;
import com.smarteldercare.modules.system.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements
        RoleService {
}