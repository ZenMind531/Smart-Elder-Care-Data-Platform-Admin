package com.smarteldercare.modules.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smarteldercare.common.exception.BusinessException;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.common.result.ResultCode;
import com.smarteldercare.modules.elderly.dto.ElderlyProfileDTO;
import com.smarteldercare.modules.elderly.entity.ElderlyProfile;
import com.smarteldercare.modules.elderly.mapper.ElderlyProfileMapper;
import com.smarteldercare.modules.elderly.service.ElderlyProfileService;
import com.smarteldercare.modules.elderly.vo.ElderlyHealthSummaryVO;
import com.smarteldercare.modules.elderly.vo.ElderlyProfileVO;
import com.smarteldercare.modules.health.entity.HealthRecord;
import com.smarteldercare.modules.health.entity.HealthWarning;
import com.smarteldercare.modules.health.mapper.HealthRecordMapper;
import com.smarteldercare.modules.health.mapper.HealthWarningMapper;
import com.smarteldercare.modules.health.vo.HealthRecordVO;
import com.smarteldercare.modules.health.vo.HealthWarningVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ElderlyProfileServiceImpl
    extends ServiceImpl<ElderlyProfileMapper, ElderlyProfile>
    implements ElderlyProfileService {

    private final HealthRecordMapper healthRecordMapper;
    private final HealthWarningMapper healthWarningMapper;

    @Override
    public PageResult<ElderlyProfileVO> listElderlyProfiles(Long page, Long size, String keyword, String gender) {
        Long currentPage = normalizePage(page);
        Long pageSize = normalizeSize(size);

        LambdaQueryWrapper<ElderlyProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), ElderlyProfile::getElderlyName, keyword)
            .eq(StringUtils.hasText(gender), ElderlyProfile::getGender, gender)
            .orderByDesc(ElderlyProfile::getCreateTime);

        Page<ElderlyProfile> result = page(new Page<>(currentPage, pageSize), wrapper);
        List<ElderlyProfileVO> records = result.getRecords().stream()
            .map(this::toVO)
            .toList();
        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public ElderlyProfileVO getElderlyProfile(Long id) {
        return toVO(getExistingById(id));
    }

    @Override
    public ElderlyHealthSummaryVO getHealthSummary(Long id) {
        ElderlyProfile elderlyProfile = getExistingById(id);

        ElderlyHealthSummaryVO summary = new ElderlyHealthSummaryVO();
        summary.setElderlyInfo(toVO(elderlyProfile));
        summary.setLatestHealthRecord(getLatestHealthRecord(id));
        summary.setRecentWarnings(getRecentWarnings(id, elderlyProfile.getElderlyName()));
        summary.setRiskLevel(elderlyProfile.getRiskLevel());
        summary.setPendingWarningCount(countPendingWarnings(id));
        return summary;
    }

    @Override
    public ElderlyProfileVO createElderlyProfile(ElderlyProfileDTO dto) {
        ElderlyProfile elderlyProfile = new ElderlyProfile();
        BeanUtils.copyProperties(dto, elderlyProfile);
        if (!StringUtils.hasText(elderlyProfile.getRiskLevel())) {
            elderlyProfile.setRiskLevel("low");
        }
        save(elderlyProfile);
        return toVO(elderlyProfile);
    }

    @Override
    public ElderlyProfileVO updateElderlyProfile(Long id, ElderlyProfileDTO dto) {
        ElderlyProfile elderlyProfile = getExistingById(id);
        BeanUtils.copyProperties(dto, elderlyProfile);
        if (!StringUtils.hasText(elderlyProfile.getRiskLevel())) {
            elderlyProfile.setRiskLevel("low");
        }
        updateById(elderlyProfile);
        return toVO(getExistingById(id));
    }

    @Override
    public void deleteElderlyProfile(Long id) {
        getExistingById(id);
        removeById(id);
    }

    private ElderlyProfile getExistingById(Long id) {
        ElderlyProfile elderlyProfile = getById(id);
        if (elderlyProfile == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return elderlyProfile;
    }

    private ElderlyProfileVO toVO(ElderlyProfile elderlyProfile) {
        ElderlyProfileVO vo = new ElderlyProfileVO();
        BeanUtils.copyProperties(elderlyProfile, vo);
        return vo;
    }

    private HealthRecordVO getLatestHealthRecord(Long elderlyId) {
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecord::getElderlyId, elderlyId)
            .orderByDesc(HealthRecord::getRecordTime)
            .orderByDesc(HealthRecord::getCreateTime)
            .last("LIMIT 1");
        HealthRecord healthRecord = healthRecordMapper.selectOne(wrapper);
        if (healthRecord == null) {
            return null;
        }

        HealthRecordVO vo = new HealthRecordVO();
        BeanUtils.copyProperties(healthRecord, vo);
        return vo;
    }

    private List<HealthWarningVO> getRecentWarnings(Long elderlyId, String elderlyName) {
        LambdaQueryWrapper<HealthWarning> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthWarning::getElderlyId, elderlyId)
            .orderByDesc(HealthWarning::getWarningTime)
            .orderByDesc(HealthWarning::getCreateTime)
            .last("LIMIT 3");
        return healthWarningMapper.selectList(wrapper).stream()
            .map(healthWarning -> toWarningVO(healthWarning, elderlyName))
            .toList();
    }

    private HealthWarningVO toWarningVO(HealthWarning healthWarning, String elderlyName) {
        HealthWarningVO vo = new HealthWarningVO();
        BeanUtils.copyProperties(healthWarning, vo);
        vo.setElderlyName(elderlyName);
        return vo;
    }

    private Long countPendingWarnings(Long elderlyId) {
        LambdaQueryWrapper<HealthWarning> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthWarning::getElderlyId, elderlyId)
            .ne(HealthWarning::getStatus, "resolved");
        return healthWarningMapper.selectCount(wrapper);
    }

    private Long normalizePage(Long page) {
        return page == null || page < 1 ? 1L : page;
    }

    private Long normalizeSize(Long size) {
        if (size == null || size < 1) {
            return 10L;
        }
        return Math.min(size, 100L);
    }
}
