package com.smarteldercare.modules.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smarteldercare.modules.device.entity.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DeviceMapper extends BaseMapper<Device> {

    @Update("UPDATE device SET elderly_id = NULL, update_time = NOW() WHERE id = #{id} AND deleted = 0")
    int unbindElderly(@Param("id") Long id);
}
