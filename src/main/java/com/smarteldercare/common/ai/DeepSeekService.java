package com.smarteldercare.common.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarteldercare.modules.elderly.entity.ElderlyProfile;
import com.smarteldercare.modules.elderly.mapper.ElderlyProfileMapper;
import com.smarteldercare.modules.health.entity.HealthRecord;
import com.smarteldercare.modules.health.entity.HealthWarning;
import com.smarteldercare.modules.health.mapper.HealthRecordMapper;
import com.smarteldercare.modules.health.mapper.HealthWarningMapper;
import com.smarteldercare.modules.device.entity.Device;
import com.smarteldercare.modules.device.mapper.DeviceMapper;
import com.smarteldercare.modules.report.entity.AssessmentReport;
import com.smarteldercare.modules.report.mapper.AssessmentReportMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class DeepSeekService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ElderlyProfileMapper elderlyProfileMapper;
    private final HealthRecordMapper healthRecordMapper;
    private final HealthWarningMapper healthWarningMapper;
    private final DeviceMapper deviceMapper;
    private final AssessmentReportMapper assessmentReportMapper;

    @Value("${DEEPSEEK_API_KEY}")
    private String apiKey;

    public DeepSeekService(ElderlyProfileMapper elderlyProfileMapper,
                           HealthRecordMapper healthRecordMapper,
                           HealthWarningMapper healthWarningMapper,
                           DeviceMapper deviceMapper,
                           AssessmentReportMapper assessmentReportMapper) {
        this.elderlyProfileMapper = elderlyProfileMapper;
        this.healthRecordMapper = healthRecordMapper;
        this.healthWarningMapper = healthWarningMapper;
        this.deviceMapper = deviceMapper;
        this.assessmentReportMapper = assessmentReportMapper;
    }

    public String generateElderlySummary(Long elderlyId) {
        ElderlyProfile elderly = elderlyProfileMapper.selectById(elderlyId);
        if (elderly == null) return "老人不存在";

        List<HealthRecord> records = healthRecordMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<HealthRecord>()
                .eq(HealthRecord::getElderlyId, elderlyId)
                .orderByDesc(HealthRecord::getRecordTime)
                .last("LIMIT 10")
        );
        List<HealthWarning> warnings = healthWarningMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<HealthWarning>()
                .eq(HealthWarning::getElderlyId, elderlyId)
                .orderByDesc(HealthWarning::getWarningTime)
                .last("LIMIT 5")
        );
        List<Device> devices = deviceMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Device>()
                .eq(Device::getElderlyId, elderlyId)
        );
        List<AssessmentReport> reports = assessmentReportMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AssessmentReport>()
                .eq(AssessmentReport::getElderlyId, elderlyId)
                .orderByDesc(AssessmentReport::getReportTime)
                .last("LIMIT 3")
        );

        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一名医生助手，请根据以下老人健康数据生成一份简短的病情总结和建议。\n\n");
        prompt.append("老人姓名：").append(elderly.getElderlyName())
            .append("，年龄：").append(elderly.getAge())
            .append("，性别：").append(elderly.getGender())
            .append("，风险等级：").append(elderly.getRiskLevel()).append("\n\n");

        if (!records.isEmpty()) {
            prompt.append("最近健康数据：\n");
            for (HealthRecord r : records) {
                prompt.append("- 时间：").append(r.getRecordTime())
                    .append("，血压：").append(r.getSystolicPressure()).append("/").append(r.getDiastolicPressure())
                    .append("，血糖：").append(r.getBloodSugar())
                    .append("，心率：").append(r.getHeartRate())
                    .append("，体温：").append(r.getTemperature()).append("\n");
            }
        }
        if (!warnings.isEmpty()) {
            prompt.append("\n最近预警：\n");
            for (HealthWarning w : warnings) {
                prompt.append("- ").append(w.getWarningContent())
                    .append("（").append(w.getWarningLevel()).append("，").append(w.getStatus()).append("）\n");
            }
        }
        if (!devices.isEmpty()) {
            prompt.append("\n绑定设备：").append(devices.size()).append("台\n");
        }
        if (!reports.isEmpty()) {
            prompt.append("\n最近评估报告：\n");
            for (AssessmentReport r : reports) {
                prompt.append("- ").append(r.getReportTitle()).append("：").append(r.getSummary()).append("\n");
            }
        }
        prompt.append("\n请给出健康评估和建议。");
        return callDeepSeek(prompt.toString());
    }

    public String generateDashboardSummary() {
        long elderlyCount = elderlyProfileMapper.selectCount(null);
        long pendingWarnings = healthWarningMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<HealthWarning>()
                .eq(HealthWarning::getStatus, "pending")
        );
        long deviceCount = deviceMapper.selectCount(null);
        long highRiskCount = elderlyProfileMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ElderlyProfile>()
                .eq(ElderlyProfile::getRiskLevel, "high")
        );

        String prompt = "你是一名养老院管理人员，请根据以下平台数据生成一段简短的总览报告：\n"
            + "老人总数：" + elderlyCount + "人\n"
            + "高风险老人：" + highRiskCount + "人\n"
            + "待处理预警：" + pendingWarnings + "条\n"
            + "设备总数：" + deviceCount + "台\n\n"
            + "请给出当前平台运营状况评估和重点关注建议。";
        return callDeepSeek(prompt);
    }

    public String chat(String message) {
        List<ElderlyProfile> allElderly = elderlyProfileMapper.selectList(null);

        StringBuilder context = new StringBuilder();
        context.append("以下是养老院所有老人的健康数据概况。用户在对话框中提问，请根据这些数据回答。\n\n");
        for (ElderlyProfile e : allElderly) {
            context.append("老人ID:").append(e.getId())
                .append("，姓名:").append(e.getElderlyName())
                .append("，年龄:").append(e.getAge())
                .append("，性别:").append(e.getGender())
                .append("，风险等级:").append(e.getRiskLevel()).append("\n");

            List<HealthRecord> records = healthRecordMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<HealthRecord>()
                    .eq(HealthRecord::getElderlyId, e.getId())
                    .orderByDesc(HealthRecord::getRecordTime).last("LIMIT 5"));
            for (HealthRecord r : records) {
                context.append("  健康记录: ").append(r.getRecordTime())
                    .append(" 血压:").append(r.getSystolicPressure()).append("/").append(r.getDiastolicPressure())
                    .append(" 血糖:").append(r.getBloodSugar())
                    .append(" 心率:").append(r.getHeartRate()).append("\n");
            }

            List<HealthWarning> warnings = healthWarningMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<HealthWarning>()
                    .eq(HealthWarning::getElderlyId, e.getId())
                    .eq(HealthWarning::getStatus, "pending"));
            for (HealthWarning w : warnings) {
                context.append("  待处理预警: ").append(w.getWarningContent()).append("\n");
            }
        }
        context.append("\n用户问题：").append(message).append("\n\n请基于以上数据回答用户的问题。");

        return callDeepSeek(context.toString());
    }

    private String callDeepSeek(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = Map.of(
                "model", "deepseek-chat",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.7,
                "max_tokens", 1024
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.deepseek.com/v1/chat/completions", request, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            return "AI 服务暂时不可用，请稍后重试。（" + e.getMessage() + "）";
        }
    }
}
