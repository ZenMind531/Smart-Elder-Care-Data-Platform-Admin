package com.smarteldercare.modules.device.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.modules.device.entity.Device;
import com.smarteldercare.modules.device.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(required = false) String status) {
        Page<Device> p = new Page<>(page, size);
        LambdaQueryWrapper<Device> q = new LambdaQueryWrapper<>();
        if (deviceType != null) q.eq(Device::getDeviceType, deviceType);
        if (status != null) q.eq(Device::getStatus, status);
        q.orderByDesc(Device::getCreateTime);
        deviceService.page(p, q);
        return ApiResponse.success(new PageResult<>(p.getRecords(), p.getTotal(), p.getCurrent(), p.getSize()));
    }

    @GetMapping("/{id}")
    public ApiResponse<Device> detail(@PathVariable Long id) {
        Device d = deviceService.getById(id);
        if (d == null) return ApiResponse.error(404, "设备不存在");
        return ApiResponse.success(d);
    }

    @PostMapping
    public ApiResponse<Void> add(@RequestBody Device device) {
        deviceService.save(device);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Device device) {
        if (deviceService.getById(id) == null) return ApiResponse.error(404, "设备不存在");
        device.setId(id);
        deviceService.updateById(device);
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
