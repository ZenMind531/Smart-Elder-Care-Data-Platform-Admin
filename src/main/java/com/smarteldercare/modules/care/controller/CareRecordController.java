package com.smarteldercare.modules.care.controller;

import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.modules.care.dto.CareRecordDTO;
import com.smarteldercare.modules.care.service.CareRecordService;
import com.smarteldercare.modules.care.vo.CareRecordVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/care-records")
@RequiredArgsConstructor
public class CareRecordController {

    private final CareRecordService careRecordService;

    @GetMapping
    public ApiResponse<PageResult<CareRecordVO>> list(
            @RequestParam(required = false, defaultValue = "1") Long page,
            @RequestParam(required = false, defaultValue = "10") Long size,
            @RequestParam(required = false) Long elderlyId,
            @RequestParam(required = false) String careType
    ) {
        return ApiResponse.success(careRecordService.listCareRecords(page, size, elderlyId, careType));
    }

    @GetMapping("/{id}")
    public ApiResponse<CareRecordVO> detail(@PathVariable Long id) {
        return ApiResponse.success(careRecordService.getCareRecord(id));
    }

    @PostMapping
    public ApiResponse<CareRecordVO> create(@Valid @RequestBody CareRecordDTO dto) {
        return ApiResponse.success(careRecordService.createCareRecord(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<CareRecordVO> update(@PathVariable Long id,
                                            @Valid @RequestBody CareRecordDTO dto) {
        return ApiResponse.success(careRecordService.updateCareRecord(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        careRecordService.deleteCareRecord(id);
        return ApiResponse.success();
    }
}
