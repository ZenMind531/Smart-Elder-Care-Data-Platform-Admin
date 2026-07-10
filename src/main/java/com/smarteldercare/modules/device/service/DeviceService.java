package com.smarteldercare.modules.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.modules.device.dto.DeviceDTO;
import com.smarteldercare.modules.device.dto.DeviceStatusDTO;
import com.smarteldercare.modules.device.entity.Device;
import com.smarteldercare.modules.device.vo.DeviceVO;
import java.util.List;

public interface DeviceService extends IService<Device> {

    PageResult<DeviceVO> listDevices(Long page, Long size, String deviceType, String status);

    DeviceVO getDevice(Long id);

    DeviceVO createDevice(DeviceDTO dto);

    DeviceVO updateDevice(Long id, DeviceDTO dto);

    void deleteDevice(Long id);

    List<DeviceVO> listDevicesByElderlyId(Long elderlyId);

    DeviceVO bindDevice(Long id, Long elderlyId);

    DeviceVO unbindDevice(Long id);

    DeviceVO updateDeviceStatus(Long id, DeviceStatusDTO dto);
}
