package com.smarteldercare.modules.health.controller;

import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.modules.health.dto.HealthRecordDTO;
import com.smarteldercare.modules.health.service.HealthRecordService;
import com.smarteldercare.modules.health.vo.HealthRecordTrendVO;
import com.smarteldercare.modules.health.vo.HealthRecordVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health-records")
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    @GetMapping
    public ApiResponse<PageResult<HealthRecordVO>> list(
        @RequestParam(required = false, defaultValue = "1") Long page,
        @RequestParam(required = false, defaultValue = "10") Long size,
        @RequestParam(required = false) Long elderlyId
    ) {
        return ApiResponse.success(healthRecordService.listHealthRecords(page, size, elderlyId));
    }

    @GetMapping("/trend")
    public ApiResponse<List<HealthRecordTrendVO>> trend(
        @RequestParam Long elderlyId,
        @RequestParam(required = false, defaultValue = "7") Integer days
    ) {
        return ApiResponse.success(healthRecordService.listHealthRecordTrend(elderlyId, days));
    }

    @GetMapping("/{id}")
    public ApiResponse<HealthRecordVO> detail(@PathVariable Long id) {
        return ApiResponse.success(healthRecordService.getHealthRecord(id));
    }

    @PostMapping
    public ApiResponse<HealthRecordVO> create(@Valid @RequestBody HealthRecordDTO dto) {
        return ApiResponse.success(healthRecordService.createHealthRecord(dto));
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        healthRecordService.deleteHealthRecord(id);
        return ApiResponse.success();
    }
}
