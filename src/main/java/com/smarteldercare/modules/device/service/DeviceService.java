package com.smarteldercare.modules.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.modules.device.entity.Device;

import java.util.List;

public interface DeviceService extends IService<Device> {

    PageResult<Device> pageDevices(Long page, Long size, String deviceType, String status, Long elderlyId);

    List<Device> listByElderlyId(Long elderlyId);

    void bindDevice(Long deviceId, Long elderlyId);

    void unbindDevice(Long deviceId);
}
