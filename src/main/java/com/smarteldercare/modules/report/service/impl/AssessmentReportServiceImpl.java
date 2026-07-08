package com.smarteldercare.modules.report.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smarteldercare.modules.report.entity.AssessmentReport;
import com.smarteldercare.modules.report.mapper.AssessmentReportMapper;
import com.smarteldercare.modules.report.service.AssessmentReportService;
import org.springframework.stereotype.Service;

@Service
public class AssessmentReportServiceImpl extends ServiceImpl<AssessmentReportMapper, AssessmentReport> implements AssessmentReportService {
}
