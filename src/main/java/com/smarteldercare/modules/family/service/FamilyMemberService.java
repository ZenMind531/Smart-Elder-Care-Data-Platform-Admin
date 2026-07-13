package com.smarteldercare.modules.family.service;

import
        com.baomidou.mybatisplus.extension.service.IService;
import
        com.smarteldercare.modules.elderly.dto.ElderlyProfileDTO;
import
        com.smarteldercare.modules.elderly.vo.ElderlyProfileVO;
import com.smarteldercare.modules.family.dto.*;
import
        com.smarteldercare.modules.family.entity.FamilyMember;
import com.smarteldercare.modules.family.vo.LoginVO;
import com.smarteldercare.modules.family.vo.ReservationVO;

import java.util.List;

public interface FamilyMemberService extends
        IService<FamilyMember> {

    // 注册
    void register(FamilyRegisterRequest request);

    // 登录
    LoginVO login(FamilyLoginRequest request);

    // 注册新老人 + 自动绑定到当前家属
    ElderlyProfileVO registerElderly(ElderlyProfileDTO
                                             dto, Long familyMemberId);

    // 绑定已有老人
    void bindElderly(BindElderlyRequest request, Long
            familyMemberId);

    // 查看我绑定的老人列表
    List<ElderlyProfileVO> getMyElderlyList(Long
                                                    familyMemberId);

    // 创建预约
    void createReservation(ReservationRequest request,
                           Long familyMemberId);

    // 查看我的预约列表
    List<ReservationVO> getMyReservationList(Long
                                                     familyMemberId);
}