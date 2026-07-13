package com.smarteldercare.modules.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smarteldercare.common.exception.BusinessException;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.common.result.ResultCode;
import com.smarteldercare.modules.device.dto.DeviceDTO;
import com.smarteldercare.modules.device.dto.DeviceStatusDTO;
import com.smarteldercare.modules.device.entity.Device;
import com.smarteldercare.modules.device.mapper.DeviceMapper;
import com.smarteldercare.modules.device.service.DeviceService;
import com.smarteldercare.modules.device.vo.DeviceVO;
import com.smarteldercare.modules.elderly.entity.ElderlyProfile;
import com.smarteldercare.modules.elderly.mapper.ElderlyProfileMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {

    private final ElderlyProfileMapper elderlyProfileMapper;

    @Override
    public PageResult<DeviceVO> listDevices(Long page, Long size, String deviceType, String status) {
        Long currentPage = normalizePage(page);
        Long pageSize = normalizeSize(size);

        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(deviceType), Device::getDeviceType, deviceType)
            .eq(StringUtils.hasText(status), Device::getStatus, status)
            .orderByDesc(Device::getCreateTime);

        Page<Device> result = page(new Page<>(currentPage, pageSize), wrapper);
        List<DeviceVO> records = result.getRecords().stream()
            .map(this::toVO)
            .toList();
        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public DeviceVO getDevice(Long id) {
        return toVO(getExistingById(id));
    }

    @Override
    public DeviceVO createDevice(DeviceDTO dto) {
        validateElderlyIfBound(dto.getElderlyId());
        Device device = new Device();
        BeanUtils.copyProperties(dto, device);
        if (!StringUtils.hasText(device.getStatus())) {
            device.setStatus("normal");
        }
        save(device);
        return toVO(device);
    }

    @Override
    public DeviceVO updateDevice(Long id, DeviceDTO dto) {
        Device device = getExistingById(id);
        validateElderlyIfBound(dto.getElderlyId());
        BeanUtils.copyProperties(dto, device);
        if (!StringUtils.hasText(device.getStatus())) {
            device.setStatus("normal");
        }
        updateById(device);
        return toVO(getExistingById(id));
    }

    @Override
    public void deleteDevice(Long id) {
        getExistingById(id);
        removeById(id);
    }

    @Override
    public List<DeviceVO> listDevicesByElderlyId(Long elderlyId) {
        getExistingElderly(elderlyId);
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getElderlyId, elderlyId)
            .orderByDesc(Device::getCreateTime);
        return list(wrapper).stream()
            .map(this::toVO)
            .toList();
    }

    @Override
    public DeviceVO bindDevice(Long id, Long elderlyId) {
        Device device = getExistingById(id);
        getExistingElderly(elderlyId);
        device.setElderlyId(elderlyId);
        updateById(device);
        return toVO(getExistingById(id));
    }

    @Override
    public DeviceVO unbindDevice(Long id) {
        getExistingById(id);
        int updated = baseMapper.unbindElderly(id);
        if (updated == 0) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return toVO(getExistingById(id));
    }

    @Override
    public DeviceVO updateDeviceStatus(Long id, DeviceStatusDTO dto) {
        Device device = getExistingById(id);
        device.setStatus(dto.getStatus());
        updateById(device);
        return toVO(getExistingById(id));
    }

    private Device getExistingById(Long id) {
        Device device = getById(id);
        if (device == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return device;
    }

    private void validateElderlyIfBound(Long elderlyId) {
        if (elderlyId != null) {
            getExistingElderly(elderlyId);
        }
    }

    private ElderlyProfile getExistingElderly(Long elderlyId) {
        ElderlyProfile elderlyProfile = elderlyProfileMapper.selectById(elderlyId);
        if (elderlyProfile == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "绑定老人不存在");
        }
        return elderlyProfile;
    }

    private DeviceVO toVO(Device device) {
        DeviceVO vo = new DeviceVO();
        BeanUtils.copyProperties(device, vo);
        if (device.getElderlyId() != null) {
            ElderlyProfile elderlyProfile = elderlyProfileMapper.selectById(device.getElderlyId());
            if (elderlyProfile != null) {
                vo.setElderlyName(elderlyProfile.getElderlyName());
            }
        }
        return vo;
    }

    private Long normalizePage(Long page) {
        return page == null || page < 1 ? 1L : page;
    }

    private Long normalizeSize(Long size) {
        if (size == null || size < 1) {
            return 10L;
        }
        return Math.min(size, 100L);
    }
}
