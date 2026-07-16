package com.smarteldercare.modules.family.controller;

import com.smarteldercare.common.exception.BusinessException;
import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.common.utils.JwtUtil;
import com.smarteldercare.modules.elderly.dto.ElderlyProfileDTO;
import com.smarteldercare.modules.elderly.vo.ElderlyProfileVO;
import com.smarteldercare.modules.family.dto.*;
import com.smarteldercare.modules.family.service.FamilyMemberService;
import com.smarteldercare.modules.family.vo.LoginVO;
import com.smarteldercare.modules.family.vo.ReservationVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/family")
public class FamilyController {

    @Autowired
    private FamilyMemberService familyMemberService;

    // ========== 工具方法：从请求头解析 JWT，拿到家属ID ==========
    private Long getFamilyMemberId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException("请先登录");
        }
        String token = authHeader.substring(7);
        return JwtUtil.getUserId(token);
    }

    // ========== 1. 家属注册 ==========
    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody FamilyRegisterRequest request) {
        familyMemberService.register(request);
        return ApiResponse.success();
    }

    // ========== 2. 家属登录 ==========
    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@RequestBody FamilyLoginRequest request) {
        LoginVO result = familyMemberService.login(request);
        return ApiResponse.success(result);
    }

    // ========== 3. 注册新老人 + 自动绑定 ==========
    @PostMapping("/elderly/register")
    public ApiResponse<ElderlyProfileVO> registerElderly(
            @RequestBody ElderlyProfileDTO dto,
            HttpServletRequest request) {
        Long familyMemberId = getFamilyMemberId(request);
        ElderlyProfileVO result = familyMemberService.registerElderly(dto,
                familyMemberId);
        return ApiResponse.success(result);
    }

    // ========== 4. 绑定已有老人 ==========
    @PostMapping("/elderly/bind")
    public ApiResponse<?> bindElderly(
            @RequestBody BindElderlyRequest req,
            HttpServletRequest request) {
        Long familyMemberId = getFamilyMemberId(request);
        familyMemberService.bindElderly(req, familyMemberId);
        return ApiResponse.success();
    }

    // ========== 5. 查看我绑定的老人列表 ==========
    @GetMapping("/elderly/list")
    public ApiResponse<List<ElderlyProfileVO>> getMyElderlyList(
            HttpServletRequest request) {
        Long familyMemberId = getFamilyMemberId(request);
        List<ElderlyProfileVO> list =
                familyMemberService.getMyElderlyList(familyMemberId);
        return ApiResponse.success(list);
    }

    // ========== 6. 创建预约 ==========
    @PostMapping("/reservation")
    public ApiResponse<ReservationVO> createReservation(
            @RequestBody ReservationRequest req,
            HttpServletRequest request) {
        Long familyMemberId = getFamilyMemberId(request);
        return ApiResponse.success(familyMemberService.createReservation(req, familyMemberId));
    }

    // ========== 7. 查看我的预约列表 ==========
    @GetMapping("/reservation/list")
    public ApiResponse<List<ReservationVO>> getMyReservationList(
            HttpServletRequest request) {
        Long familyMemberId = getFamilyMemberId(request);
        List<ReservationVO> list =
                familyMemberService.getMyReservationList(familyMemberId);
        return ApiResponse.success(list);
    }
}
