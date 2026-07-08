package com.smarteldercare.modules.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smarteldercare.modules.elderly.entity.ElderlyProfile;
import com.smarteldercare.modules.elderly.mapper.ElderlyProfileMapper;
import com.smarteldercare.modules.health.entity.HealthWarning;
import com.smarteldercare.modules.health.mapper.HealthWarningMapper;
import com.smarteldercare.modules.health.service.HealthRiskLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthRiskLevelServiceImpl implements HealthRiskLevelService {

    private final HealthWarningMapper healthWarningMapper;
    private final ElderlyProfileMapper elderlyProfileMapper;

    @Override
    public void recalculateRiskLevel(Long elderlyId) {
        Long highCount = countUnresolvedWarnings(elderlyId, "high");
        Long mediumCount = countUnresolvedWarnings(elderlyId, "medium");

        String riskLevel = "low";
        if (highCount >= 1) {
            riskLevel = "high";
        } else if (mediumCount >= 2) {
            riskLevel = "medium";
        }

        ElderlyProfile elderlyProfile = new ElderlyProfile();
        elderlyProfile.setId(elderlyId);
        elderlyProfile.setRiskLevel(riskLevel);
        elderlyProfileMapper.updateById(elderlyProfile);
    }

    private Long countUnresolvedWarnings(Long elderlyId, String warningLevel) {
        LambdaQueryWrapper<HealthWarning> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthWarning::getElderlyId, elderlyId)
            .eq(HealthWarning::getWarningLevel, warningLevel)
            .ne(HealthWarning::getStatus, "resolved");
        return healthWarningMapper.selectCount(wrapper);
    }
}
