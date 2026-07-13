package com.smarteldercare.modules.health.controller;

import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.modules.health.dto.HealthWarningDTO;
import com.smarteldercare.modules.health.dto.HealthWarningStatusDTO;
import com.smarteldercare.modules.health.service.HealthWarningService;
import com.smarteldercare.modules.health.vo.HealthWarningVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health-warnings")
@RequiredArgsConstructor
public class HealthWarningController {

    private final HealthWarningService healthWarningService;

    @GetMapping
    public ApiResponse<PageResult<HealthWarningVO>> list(
        @RequestParam(required = false, defaultValue = "1") Long page,
        @RequestParam(required = false, defaultValue = "10") Long size,
        @RequestParam(required = false) Long elderlyId,
        @RequestParam(required = false) String warningLevel,
        @RequestParam(required = false) String status
    ) {
        return ApiResponse.success(healthWarningService.listHealthWarnings(page, size, elderlyId, warningLevel, status));
    }

    @GetMapping("/{id}")
    public ApiResponse<HealthWarningVO> detail(@PathVariable Long id) {
        return ApiResponse.success(healthWarningService.getHealthWarning(id));
    }

    @PostMapping
    public ApiResponse<HealthWarningVO> create(@Valid @RequestBody HealthWarningDTO dto) {
        return ApiResponse.success(healthWarningService.createHealthWarning(dto));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<HealthWarningVO> updateStatus(
        @PathVariable Long id,
        @Valid @RequestBody HealthWarningStatusDTO dto
    ) {
        return ApiResponse.success(healthWarningService.updateHealthWarningStatus(id, dto));
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        healthWarningService.deleteHealthWarning(id);
        return ApiResponse.success();
    }
}
