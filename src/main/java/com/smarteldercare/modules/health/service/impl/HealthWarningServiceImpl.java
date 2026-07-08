package com.smarteldercare.modules.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smarteldercare.common.exception.BusinessException;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.common.result.ResultCode;
import com.smarteldercare.modules.elderly.entity.ElderlyProfile;
import com.smarteldercare.modules.elderly.mapper.ElderlyProfileMapper;
import com.smarteldercare.modules.health.dto.HealthWarningDTO;
import com.smarteldercare.modules.health.dto.HealthWarningStatusDTO;
import com.smarteldercare.modules.health.entity.HealthWarning;
import com.smarteldercare.modules.health.mapper.HealthWarningMapper;
import com.smarteldercare.modules.health.service.HealthRiskLevelService;
import com.smarteldercare.modules.health.service.HealthWarningService;
import com.smarteldercare.modules.health.vo.HealthWarningVO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class HealthWarningServiceImpl
    extends ServiceImpl<HealthWarningMapper, HealthWarning>
    implements HealthWarningService {

    private final ElderlyProfileMapper elderlyProfileMapper;
    private final HealthRiskLevelService healthRiskLevelService;

    @Override
    public PageResult<HealthWarningVO> listHealthWarnings(
        Long page,
        Long size,
        Long elderlyId,
        String warningLevel,
        String status
    ) {
        Long currentPage = normalizePage(page);
        Long pageSize = normalizeSize(size);

        LambdaQueryWrapper<HealthWarning> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(elderlyId != null, HealthWarning::getElderlyId, elderlyId)
            .eq(StringUtils.hasText(warningLevel), HealthWarning::getWarningLevel, warningLevel)
            .eq(StringUtils.hasText(status), HealthWarning::getStatus, status)
            .orderByDesc(HealthWarning::getWarningTime)
            .orderByDesc(HealthWarning::getCreateTime);

        Page<HealthWarning> result = page(new Page<>(currentPage, pageSize), wrapper);
        List<HealthWarningVO> records = result.getRecords().stream()
            .map(this::toVO)
            .toList();
        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public HealthWarningVO getHealthWarning(Long id) {
        return toVO(getExistingById(id));
    }

    @Override
    public HealthWarningVO createHealthWarning(HealthWarningDTO dto) {
        ElderlyProfile elderlyProfile = getExistingElderly(dto.getElderlyId());
        HealthWarning healthWarning = new HealthWarning();
        BeanUtils.copyProperties(dto, healthWarning);
        healthWarning.setStatus("pending");
        if (healthWarning.getWarningTime() == null) {
            healthWarning.setWarningTime(LocalDateTime.now());
        }
        save(healthWarning);
        return toVO(healthWarning, elderlyProfile);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HealthWarningVO updateHealthWarningStatus(Long id, HealthWarningStatusDTO dto) {
        HealthWarning healthWarning = getExistingById(id);
        healthWarning.setStatus(dto.getStatus());
        healthWarning.setHandleResult(dto.getHandleResult());
        if ("resolved".equals(dto.getStatus())) {
            healthWarning.setHandleTime(LocalDateTime.now());
        } else {
            healthWarning.setHandleTime(null);
        }
        updateById(healthWarning);
        if ("resolved".equals(dto.getStatus())) {
            healthRiskLevelService.recalculateRiskLevel(healthWarning.getElderlyId());
        }
        return toVO(getExistingById(id));
    }

    @Override
    public void deleteHealthWarning(Long id) {
        getExistingById(id);
        removeById(id);
    }

    private HealthWarning getExistingById(Long id) {
        HealthWarning healthWarning = getById(id);
        if (healthWarning == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return healthWarning;
    }

    private ElderlyProfile getExistingElderly(Long elderlyId) {
        ElderlyProfile elderlyProfile = elderlyProfileMapper.selectById(elderlyId);
        if (elderlyProfile == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return elderlyProfile;
    }

    private HealthWarningVO toVO(HealthWarning healthWarning) {
        return toVO(healthWarning, getExistingElderly(healthWarning.getElderlyId()));
    }

    private HealthWarningVO toVO(HealthWarning healthWarning, ElderlyProfile elderlyProfile) {
        HealthWarningVO vo = new HealthWarningVO();
        BeanUtils.copyProperties(healthWarning, vo);
        vo.setElderlyName(elderlyProfile.getElderlyName());
        return vo;
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
