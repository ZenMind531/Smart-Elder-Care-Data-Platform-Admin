package com.smarteldercare.modules.ai.controller;

import com.smarteldercare.common.ai.DeepSeekService;
import com.smarteldercare.common.result.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final DeepSeekService deepSeekService;

    public AiController(DeepSeekService deepSeekService) {
        this.deepSeekService = deepSeekService;
    }

    @PostMapping("/elderly-summary/{elderlyId}")
    public ApiResponse<Map<String, String>> elderlySummary(@PathVariable Long elderlyId) {
        String summary = deepSeekService.generateElderlySummary(elderlyId);
        return ApiResponse.success(Map.of("summary", summary));
    }

    @PostMapping("/dashboard-summary")
    public ApiResponse<Map<String, String>> dashboardSummary() {
        String summary = deepSeekService.generateDashboardSummary();
        return ApiResponse.success(Map.of("summary", summary));
    }
}
