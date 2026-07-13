package com.smarteldercare.modules.elderly.controller;

import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.modules.elderly.dto.ElderlyProfileDTO;
import com.smarteldercare.modules.elderly.service.ElderlyProfileService;
import com.smarteldercare.modules.elderly.vo.ElderlyHealthSummaryVO;
import com.smarteldercare.modules.elderly.vo.ElderlyImportResultVO;
import com.smarteldercare.modules.elderly.vo.ElderlyProfileVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/elderly")
@RequiredArgsConstructor
public class ElderlyProfileController {

    private final ElderlyProfileService elderlyProfileService;

    @GetMapping
    public ApiResponse<PageResult<ElderlyProfileVO>> list(
        @RequestParam(required = false, defaultValue = "1") Long page,
        @RequestParam(required = false, defaultValue = "10") Long size,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String gender
    ) {
        return ApiResponse.success(elderlyProfileService.listElderlyProfiles(page, size, keyword, gender));
    }

    @GetMapping("/{id}")
    public ApiResponse<ElderlyProfileVO> detail(@PathVariable Long id) {
        return ApiResponse.success(elderlyProfileService.getElderlyProfile(id));
    }

    @GetMapping("/{id}/health-summary")
    public ApiResponse<ElderlyHealthSummaryVO> healthSummary(@PathVariable Long id) {
        return ApiResponse.success(elderlyProfileService.getHealthSummary(id));
    }

    @GetMapping(
        value = "/export",
        produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    public void exportExcel(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String gender,
        HttpServletResponse response
    ) {
        elderlyProfileService.exportElderlyProfiles(response, keyword, gender);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ElderlyImportResultVO> importExcel(@RequestParam("file") MultipartFile file) {
        return ApiResponse.success(elderlyProfileService.importElderlyProfiles(file));
    }

    @PostMapping
    public ApiResponse<ElderlyProfileVO> create(@Valid @RequestBody ElderlyProfileDTO dto) {
        return ApiResponse.success(elderlyProfileService.createElderlyProfile(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<ElderlyProfileVO> update(@PathVariable Long id, @Valid @RequestBody ElderlyProfileDTO dto) {
        return ApiResponse.success(elderlyProfileService.updateElderlyProfile(id, dto));
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        elderlyProfileService.deleteElderlyProfile(id);
        return ApiResponse.success();
    }
}
