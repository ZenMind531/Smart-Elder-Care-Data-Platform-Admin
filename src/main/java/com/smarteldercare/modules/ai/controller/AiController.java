package com.smarteldercare.modules.ai.controller;

import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.modules.ai.dto.ChatRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final com.smarteldercare.common.ai.DeepSeekService deepSeekService;

    public AiController(com.smarteldercare.common.ai.DeepSeekService deepSeekService) {
        this.deepSeekService = deepSeekService;
    }

    @PostMapping("/chat")
    public ApiResponse<Map<String, String>> chat(@RequestBody ChatRequest request) {
        String reply = deepSeekService.chat(request.getMessage());
        return ApiResponse.success(Map.of("reply", reply));
    }
}
