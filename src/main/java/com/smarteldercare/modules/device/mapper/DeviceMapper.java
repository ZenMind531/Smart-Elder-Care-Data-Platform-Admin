package com.smarteldercare.modules.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smarteldercare.modules.device.entity.Device;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
}
