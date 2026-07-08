package com.smarteldercare.modules.device.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smarteldercare.modules.device.entity.Device;
import com.smarteldercare.modules.device.mapper.DeviceMapper;
import com.smarteldercare.modules.device.service.DeviceService;
import org.springframework.stereotype.Service;

@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {
}
