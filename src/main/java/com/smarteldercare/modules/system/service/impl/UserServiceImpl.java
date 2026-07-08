package com.smarteldercare.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smarteldercare.modules.system.entity.User;
import com.smarteldercare.modules.system.mapper.UserMapper;
import com.smarteldercare.modules.system.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements
        UserService {
}