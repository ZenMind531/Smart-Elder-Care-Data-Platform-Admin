package com.smarteldercare.modules.family.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smarteldercare.common.exception.BusinessException;
import com.smarteldercare.common.utils.JwtUtil;
import com.smarteldercare.modules.elderly.dto.ElderlyProfileDTO;
import com.smarteldercare.modules.elderly.entity.ElderlyProfile;
import com.smarteldercare.modules.elderly.mapper.ElderlyProfileMapper;
import com.smarteldercare.modules.elderly.vo.ElderlyProfileVO;
import com.smarteldercare.modules.family.dto.*;
import com.smarteldercare.modules.family.entity.FamilyMember;
import com.smarteldercare.modules.family.entity.ServiceReservation;
import com.smarteldercare.modules.family.mapper.FamilyMemberMapper;
import com.smarteldercare.modules.family.mapper.ServiceReservationMapper;
import com.smarteldercare.modules.family.service.FamilyMemberService;
import com.smarteldercare.modules.family.vo.LoginVO;
import com.smarteldercare.modules.family.vo.ReservationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyMemberServiceImpl
        extends ServiceImpl<FamilyMemberMapper, FamilyMember>
        implements FamilyMemberService {

    private final ElderlyProfileMapper elderlyProfileMapper;
    private final ServiceReservationMapper serviceReservationMapper;

    // ========== 1. 注册 ==========
    @Override
    public void register(FamilyRegisterRequest request) {
        LambdaQueryWrapper<FamilyMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FamilyMember::getPhone, request.getPhone());
        if (this.baseMapper.selectOne(wrapper) != null) {
            throw new BusinessException("手机号已注册");
        }
        FamilyMember member = new FamilyMember();
        member.setPhone(request.getPhone());
        member.setPassword(request.getPassword());
        member.setName(request.getName());
        member.setStatus("enabled");
        this.baseMapper.insert(member);
    }

    // ========== 2. 登录 ==========
    @Override
    public LoginVO login(FamilyLoginRequest request) {
        LambdaQueryWrapper<FamilyMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FamilyMember::getPhone, request.getPhone());
        FamilyMember member = this.baseMapper.selectOne(wrapper);
        if (member == null) {
            throw new BusinessException("手机号未注册");
        }
        if (!member.getPassword().equals(request.getPassword())) {
            throw new BusinessException("密码错误");
        }
        if ("disabled".equals(member.getStatus())) {
            throw new BusinessException("账号已禁用");
        }
        String token = JwtUtil.generate(member.getId(), member.getPhone());
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setId(member.getId());
        vo.setName(member.getName());
        return vo;
    }

    // ========== 3. 注册新老人 + 自动绑定 ==========
    @Override
    public ElderlyProfileVO registerElderly(ElderlyProfileDTO dto, Long
            familyMemberId) {
        ElderlyProfile profile = new ElderlyProfile();
        BeanUtils.copyProperties(dto, profile);
        profile.setFamilyMemberId(familyMemberId);
        if (profile.getRiskLevel() == null) {
            profile.setRiskLevel("low");
        }
        elderlyProfileMapper.insert(profile);
        ElderlyProfileVO vo = new ElderlyProfileVO();
        BeanUtils.copyProperties(profile, vo);
        return vo;
    }

    // ========== 4. 绑定已有老人 ==========
    @Override
    public void bindElderly(BindElderlyRequest request, Long familyMemberId) {
        ElderlyProfile profile =
                elderlyProfileMapper.selectById(request.getElderlyId());
        if (profile == null) {
            throw new BusinessException("老人不存在");
        }
        if (profile.getFamilyMemberId() != null) {
            throw new BusinessException("该老人已被其他家属绑定");
        }
        profile.setFamilyMemberId(familyMemberId);
        elderlyProfileMapper.updateById(profile);
    }

    // ========== 5. 查看我绑定的老人列表 ==========
    @Override
    public List<ElderlyProfileVO> getMyElderlyList(Long familyMemberId) {
        LambdaQueryWrapper<ElderlyProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ElderlyProfile::getFamilyMemberId, familyMemberId);
        return elderlyProfileMapper.selectList(wrapper).stream().map(profile -> {
            ElderlyProfileVO vo = new ElderlyProfileVO();
            BeanUtils.copyProperties(profile, vo);
            return vo;
        }).toList();
    }

    // ========== 6. 创建预约 ==========
    @Override
    public void createReservation(ReservationRequest request, Long familyMemberId) {
        ServiceReservation reservation = new ServiceReservation();
        BeanUtils.copyProperties(request, reservation);
        reservation.setFamilyMemberId(familyMemberId);
        reservation.setStatus("pending");
        serviceReservationMapper.insert(reservation);
    }

    // ========== 7. 查看我的预约列表 ==========
    @Override
    public List<ReservationVO> getMyReservationList(Long familyMemberId) {
        LambdaQueryWrapper<ServiceReservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceReservation::getFamilyMemberId, familyMemberId)
                .orderByDesc(ServiceReservation::getCreateTime);
        return serviceReservationMapper.selectList(wrapper).stream().map(r -> {
            ReservationVO vo = new ReservationVO();
            BeanUtils.copyProperties(r, vo);
            ElderlyProfile profile =
                    elderlyProfileMapper.selectById(r.getElderlyId());
            vo.setElderlyName(profile != null ? profile.getElderlyName() : "未知");
            return vo;
        }).toList();
    }
}