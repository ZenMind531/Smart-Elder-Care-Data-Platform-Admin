package com.smarteldercare.modules.device.controller;

import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.modules.device.dto.DeviceBindDTO;
import com.smarteldercare.modules.device.dto.DeviceDTO;
import com.smarteldercare.modules.device.entity.Device;
import com.smarteldercare.modules.device.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping
    public ApiResponse<PageResult<Device>> list(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String deviceType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long elderlyId) {
        return ApiResponse.success(deviceService.pageDevices(page, size, deviceType, status, elderlyId));
    }

    @GetMapping("/{id}")
    public ApiResponse<Device> detail(@PathVariable Long id) {
        Device d = deviceService.getById(id);
        if (d == null) return ApiResponse.error(404, "设备不存在");
        return ApiResponse.success(d);
    }

    @GetMapping("/elderly/{elderlyId}")
    public ApiResponse<List<Device>> listByElderly(@PathVariable Long elderlyId) {
        return ApiResponse.success(deviceService.listByElderlyId(elderlyId));
    }

    @PostMapping
    public ApiResponse<Void> add(@Valid @RequestBody DeviceDTO dto) {
        Device device = new Device();
        device.setDeviceCode(dto.getDeviceCode());
        device.setDeviceName(dto.getDeviceName());
        device.setDeviceType(dto.getDeviceType());
        device.setStatus(dto.getStatus() != null ? dto.getStatus() : "normal");
        device.setRemark(dto.getRemark());
        deviceService.save(device);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody DeviceDTO dto) {
        Device device = deviceService.getById(id);
        if (device == null) return ApiResponse.error(404, "设备不存在");
        device.setDeviceCode(dto.getDeviceCode());
        device.setDeviceName(dto.getDeviceName());
        device.setDeviceType(dto.getDeviceType());
        device.setStatus(dto.getStatus() != null ? dto.getStatus() : "normal");
        device.setRemark(dto.getRemark());
        deviceService.updateById(device);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/bind")
    public ApiResponse<Void> bind(@PathVariable Long id, @Valid @RequestBody DeviceBindDTO dto) {
        deviceService.bindDevice(id, dto.getElderlyId());
        return ApiResponse.success();
    }

    @PutMapping("/{id}/unbind")
    public ApiResponse<Void> unbind(@PathVariable Long id) {
        deviceService.unbindDevice(id);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        if (deviceService.getById(id) == null) return ApiResponse.error(404, "设备不存在");
        deviceService.removeById(id);
        return ApiResponse.success();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestBody Device device) {
        Device d = deviceService.getById(id);
        if (d == null) return ApiResponse.error(404, "设备不存在");
        d.setStatus(device.getStatus());
        deviceService.updateById(d);
        return ApiResponse.success();
    }
}
