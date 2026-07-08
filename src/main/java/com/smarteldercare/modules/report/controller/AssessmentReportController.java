package com.smarteldercare.modules.report.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.modules.report.entity.AssessmentReport;
import com.smarteldercare.modules.report.service.AssessmentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessment-reports")
public class AssessmentReportController {

    @Autowired
    private AssessmentReportService assessmentReportService;

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
        return ApiResponse.success(new PageResult<>(p.getRecords(), p.getTotal(), p.getCurrent(), p.getSize()));
    }

    @GetMapping("/{id}")
    public ApiResponse<AssessmentReport> detail(@PathVariable Long id) {
        AssessmentReport r = assessmentReportService.getById(id);
        if (r == null) return ApiResponse.error(404, "报告不存在");
        return ApiResponse.success(r);
    }

    @PostMapping
    public ApiResponse<Void> add(@RequestBody AssessmentReport report) {
        assessmentReportService.save(report);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody AssessmentReport report) {
        report.setId(id);
        assessmentReportService.updateById(report);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        assessmentReportService.removeById(id);
        return ApiResponse.success();
    }
}
