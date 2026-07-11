package com.smarteldercare.modules.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smarteldercare.common.exception.BusinessException;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.common.result.ResultCode;
import com.smarteldercare.modules.device.entity.Device;
import com.smarteldercare.modules.device.mapper.DeviceMapper;
import com.smarteldercare.modules.device.service.DeviceService;
import com.smarteldercare.modules.elderly.entity.ElderlyProfile;
import com.smarteldercare.modules.elderly.mapper.ElderlyProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {

    @Autowired
    private ElderlyProfileMapper elderlyProfileMapper;

    @Override
    public PageResult<Device> pageDevices(Long page, Long size, String deviceType, String status, Long elderlyId) {
        Page<Device> p = new Page<>(page, size);
        LambdaQueryWrapper<Device> q = new LambdaQueryWrapper<>();
        if (deviceType != null) {
            q.eq(Device::getDeviceType, deviceType);
        }
        if (status != null) {
            q.eq(Device::getStatus, status);
        }
        if (elderlyId != null) {
            q.eq(Device::getElderlyId, elderlyId);
        }
        q.orderByDesc(Device::getCreateTime);
        this.page(p, q);
        return new PageResult<>(p.getRecords(), p.getTotal(), p.getCurrent(), p.getSize());
    }

    @Override
    public List<Device> listByElderlyId(Long elderlyId) {
        LambdaQueryWrapper<Device> q = new LambdaQueryWrapper<>();
        q.eq(Device::getElderlyId, elderlyId);
        q.orderByDesc(Device::getCreateTime);
        return this.list(q);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindDevice(Long deviceId, Long elderlyId) {
        // 1. 检查设备是否存在
        Device device = this.getById(deviceId);
        if (device == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 2. 检查设备是否已被其他老人绑定
        if (device.getElderlyId() != null && !device.getElderlyId().equals(elderlyId)) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }

        // 3. 检查老人是否存在（逻辑删除的也算不存在）
        ElderlyProfile elderly = elderlyProfileMapper.selectOne(
                new LambdaQueryWrapper<ElderlyProfile>()
                        .eq(ElderlyProfile::getId, elderlyId)
                        .eq(ElderlyProfile::getDeleted, 0)
        );
        if (elderly == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 4. 执行绑定
        device.setElderlyId(elderlyId);
        this.updateById(device);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindDevice(Long deviceId) {
        // 1. 检查设备是否存在
        Device device = this.getById(deviceId);
        if (device == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 2. 检查设备是否已绑定老人
        if (device.getElderlyId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }

        // 3. 解绑
        device.setElderlyId(null);
        this.updateById(device);
    }
}
