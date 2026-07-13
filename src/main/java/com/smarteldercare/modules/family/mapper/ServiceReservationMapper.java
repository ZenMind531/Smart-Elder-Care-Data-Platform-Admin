package com.smarteldercare.modules.family.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smarteldercare.modules.family.entity.ServiceReservation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServiceReservationMapper extends
        BaseMapper<ServiceReservation> {
}