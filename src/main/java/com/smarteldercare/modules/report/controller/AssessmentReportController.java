package com.smarteldercare.modules.report.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.modules.report.entity.AssessmentReport;
import com.smarteldercare.modules.report.service.AssessmentReportService;
import com.smarteldercare.modules.elderly.mapper.ElderlyProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessment-reports")
public class AssessmentReportController {

    @Autowired
    private AssessmentReportService assessmentReportService;

    @Autowired
    private ElderlyProfileMapper elderlyProfileMapper;

    private void fillElderlyName(AssessmentReport r) {
        if (r != null && r.getElderlyId() != null) {
            com.smarteldercare.modules.elderly.entity.ElderlyProfile e = elderlyProfileMapper.selectById(r.getElderlyId());
            if (e != null) r.setElderlyName(e.getElderlyName());
        }
    }

    @GetMapping
    public ApiResponse<PageResult<AssessmentReport>> list(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long elderlyId,
            @RequestParam(required = false) String riskLevel) {
        Page<AssessmentReport> p = new Page<>(page, size);
        LambdaQueryWrapper<AssessmentReport> q = new LambdaQueryWrapper<>();
        if (elderlyId != null) q.eq(AssessmentReport::getElderlyId, elderlyId);
        if (riskLevel != null) q.eq(AssessmentReport::getRiskLevel, riskLevel);
        q.orderByDesc(AssessmentReport::getCreateTime);
        assessmentReportService.page(p, q);
        p.getRecords().forEach(this::fillElderlyName);
        return ApiResponse.success(new PageResult<>(p.getRecords(), p.getTotal(), p.getCurrent(), p.getSize()));
    }

    @GetMapping("/{id}")
    public ApiResponse<AssessmentReport> detail(@PathVariable Long id) {
        AssessmentReport r = assessmentReportService.getById(id);
        if (r == null) return ApiResponse.error(404, "报告不存在");
        fillElderlyName(r);
        return ApiResponse.success(r);
    }

    @PostMapping
    public ApiResponse<Void> add(@RequestBody AssessmentReport report) {
        assessmentReportService.save(report);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody AssessmentReport report) {
        if (assessmentReportService.getById(id) == null) return ApiResponse.error(404, "报告不存在");
        report.setId(id);
        assessmentReportService.updateById(report);
        return ApiResponse.success();
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        if (assessmentReportService.getById(id) == null) return ApiResponse.error(404, "报告不存在");
        assessmentReportService.removeById(id);
        return ApiResponse.success();
    }
}
