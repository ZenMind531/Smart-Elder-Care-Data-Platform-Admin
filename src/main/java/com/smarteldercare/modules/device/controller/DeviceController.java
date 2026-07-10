package com.smarteldercare.modules.device.controller;

import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.modules.device.dto.DeviceDTO;
import com.smarteldercare.modules.device.dto.DeviceStatusDTO;
import com.smarteldercare.modules.device.service.DeviceService;
import com.smarteldercare.modules.device.vo.DeviceVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public ApiResponse<PageResult<DeviceVO>> list(
        @RequestParam(required = false, defaultValue = "1") Long page,
        @RequestParam(required = false, defaultValue = "10") Long size,
        @RequestParam(required = false) String deviceType,
        @RequestParam(required = false) String status
    ) {
        return ApiResponse.success(deviceService.listDevices(page, size, deviceType, status));
    }

    @GetMapping("/{id}")
    public ApiResponse<DeviceVO> detail(@PathVariable Long id) {
        return ApiResponse.success(deviceService.getDevice(id));
    }

    @PostMapping
    public ApiResponse<DeviceVO> create(@Valid @RequestBody DeviceDTO dto) {
        return ApiResponse.success(deviceService.createDevice(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<DeviceVO> update(@PathVariable Long id, @Valid @RequestBody DeviceDTO dto) {
        return ApiResponse.success(deviceService.updateDevice(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ApiResponse.success();
    }

    @GetMapping("/elderly/{elderlyId}")
    public ApiResponse<List<DeviceVO>> listByElderlyId(@PathVariable Long elderlyId) {
        return ApiResponse.success(deviceService.listDevicesByElderlyId(elderlyId));
    }

    @PatchMapping("/{id}/bind/{elderlyId}")
    public ApiResponse<DeviceVO> bind(@PathVariable Long id, @PathVariable Long elderlyId) {
        return ApiResponse.success(deviceService.bindDevice(id, elderlyId));
    }

    @PatchMapping("/{id}/unbind")
    public ApiResponse<DeviceVO> unbind(@PathVariable Long id) {
        return ApiResponse.success(deviceService.unbindDevice(id));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<DeviceVO> updateStatus(@PathVariable Long id, @Valid @RequestBody DeviceStatusDTO dto) {
        return ApiResponse.success(deviceService.updateDeviceStatus(id, dto));
    }
}
