package com.smarteldercare.modules.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smarteldercare.common.exception.BusinessException;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.common.result.ResultCode;
import com.smarteldercare.modules.elderly.entity.ElderlyProfile;
import com.smarteldercare.modules.elderly.mapper.ElderlyProfileMapper;
import com.smarteldercare.modules.health.dto.HealthRecordDTO;
import com.smarteldercare.modules.health.entity.HealthRecord;
import com.smarteldercare.modules.health.entity.HealthWarning;
import com.smarteldercare.modules.health.mapper.HealthRecordMapper;
import com.smarteldercare.modules.health.mapper.HealthWarningMapper;
import com.smarteldercare.modules.health.service.HealthRecordService;
import com.smarteldercare.modules.health.service.HealthRiskLevelService;
import com.smarteldercare.modules.health.vo.HealthRecordTrendVO;
import com.smarteldercare.modules.health.vo.HealthRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthRecordServiceImpl
    extends ServiceImpl<HealthRecordMapper, HealthRecord>
    implements HealthRecordService {

    private final ElderlyProfileMapper elderlyProfileMapper;
    private final HealthWarningMapper healthWarningMapper;
    private final HealthRiskLevelService healthRiskLevelService;

    @Override
    public PageResult<HealthRecordVO> listHealthRecords(Long page, Long size, Long elderlyId) {
        Long currentPage = normalizePage(page);
        Long pageSize = normalizeSize(size);

        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(elderlyId != null, HealthRecord::getElderlyId, elderlyId)
            .orderByDesc(HealthRecord::getRecordTime)
            .orderByDesc(HealthRecord::getCreateTime);

        Page<HealthRecord> result = page(new Page<>(currentPage, pageSize), wrapper);
        List<HealthRecordVO> records = result.getRecords().stream()
            .map(this::toVO)
            .toList();
        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public List<HealthRecordTrendVO> listHealthRecordTrend(Long elderlyId, Integer days) {
        if (elderlyProfileMapper.selectById(elderlyId) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        int dayRange = normalizeDays(days);
        LocalDateTime startTime = LocalDateTime.now().minusDays(dayRange);

        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecord::getElderlyId, elderlyId)
            .ge(HealthRecord::getRecordTime, startTime)
            .orderByAsc(HealthRecord::getRecordTime);

        return list(wrapper).stream()
            .map(this::toTrendVO)
            .toList();
    }

    @Override
    public HealthRecordVO getHealthRecord(Long id) {
        return toVO(getExistingById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HealthRecordVO createHealthRecord(HealthRecordDTO dto) {
        Long elderlyId = resolveElderlyId(dto);
        HealthRecord healthRecord = new HealthRecord();
        BeanUtils.copyProperties(dto, healthRecord);
        healthRecord.setElderlyId(elderlyId);
        save(healthRecord);
        if (dto.getRetestWarningId() != null) {
            handleRetestWarning(dto.getRetestWarningId(), healthRecord);
        } else {
            checkHealthRecordAndCreateWarning(healthRecord);
        }
        healthRiskLevelService.recalculateRiskLevel(healthRecord.getElderlyId());
        return toVO(healthRecord);
    }

    private Long resolveElderlyId(HealthRecordDTO dto) {
        if (dto.getElderlyId() != null) {
            if (elderlyProfileMapper.selectById(dto.getElderlyId()) == null) {
                throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "老人不存在");
            }
            return dto.getElderlyId();
        }

        String elderlyName = dto.getElderlyName();
        if (elderlyName == null || elderlyName.trim().isEmpty()) {
            throw new BusinessException("elderlyId 和 elderlyName 不能同时为空");
        }

        LambdaQueryWrapper<ElderlyProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ElderlyProfile::getElderlyName, elderlyName.trim());
        List<ElderlyProfile> matches = elderlyProfileMapper.selectList(wrapper);

        if (matches.size() == 1) {
            return matches.get(0).getId();
        }
        if (matches.size() > 1) {
            String ids = matches.stream()
                .map(ElderlyProfile::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
            throw new BusinessException("存在同名老人，请选择老人ID：" + ids);
        }

        ElderlyProfile elderlyProfile = new ElderlyProfile();
        elderlyProfile.setElderlyName(elderlyName.trim());
        elderlyProfile.setGender("unknown");
        elderlyProfile.setAge(0);
        elderlyProfile.setRiskLevel("low");
        elderlyProfileMapper.insert(elderlyProfile);
        return elderlyProfile.getId();
    }

    @Override
    public void deleteHealthRecord(Long id) {
        getExistingById(id);
        removeById(id);
    }

    private HealthRecord getExistingById(Long id) {
        HealthRecord healthRecord = getById(id);
        if (healthRecord == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return healthRecord;
    }

    private HealthRecordVO toVO(HealthRecord healthRecord) {
        HealthRecordVO vo = new HealthRecordVO();
        BeanUtils.copyProperties(healthRecord, vo);
        if (healthRecord.getElderlyId() != null) {
            com.smarteldercare.modules.elderly.entity.ElderlyProfile e = elderlyProfileMapper.selectById(healthRecord.getElderlyId());
            if (e != null) vo.setElderlyName(e.getElderlyName());
        }
        return vo;
    }

    private HealthRecordTrendVO toTrendVO(HealthRecord healthRecord) {
        HealthRecordTrendVO vo = new HealthRecordTrendVO();
        BeanUtils.copyProperties(healthRecord, vo);
        return vo;
    }

    private void checkHealthRecordAndCreateWarning(HealthRecord record) {
        checkBloodPressure(record);
        checkBloodSugar(record);
        checkHeartRate(record);
        checkTemperature(record);
    }

    private void handleRetestWarning(Long warningId, HealthRecord retestRecord) {
        HealthWarning warning = healthWarningMapper.selectById(warningId);
        if (warning == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "复测预警不存在");
        }
        if (!warning.getElderlyId().equals(retestRecord.getElderlyId())) {
            throw new BusinessException("复测记录和预警不属于同一个老人");
        }
        if (!hasMeasurementForWarningType(retestRecord, warning.getWarningType())) {
            throw new BusinessException("复测数据缺少对应的指标：" + warning.getWarningType());
        }

        warning.setRetestRecordId(retestRecord.getId());
        if (isNormalForWarningType(retestRecord, warning.getWarningType())) {
            warning.setStatus("resolved");
            warning.setHandleResult("复测结果正常，系统自动处理");
            warning.setHandleTime(retestRecord.getRecordTime() == null ? LocalDateTime.now() : retestRecord.getRecordTime());
        } else {
            warning.setStatus("processing");
            warning.setHandleResult("复测结果仍异常，请继续跟进");
            warning.setHandleTime(null);
        }
        healthWarningMapper.updateById(warning);
    }

    private void checkBloodPressure(HealthRecord record) {
        Integer systolicPressure = record.getSystolicPressure();
        Integer diastolicPressure = record.getDiastolicPressure();
        if (!hasBloodPressure(record)) {
            return;
        }

        if (greaterThanOrEqual(systolicPressure, 160) || greaterThanOrEqual(diastolicPressure, 100)) {
            createWarning(record, "blood_pressure", "high", "Blood pressure is high");
            return;
        }
        if (greaterThanOrEqual(systolicPressure, 140) || greaterThanOrEqual(diastolicPressure, 90)) {
            createWarning(record, "blood_pressure", "medium", "Blood pressure is above normal range");
        }
    }

    private void checkBloodSugar(HealthRecord record) {
        BigDecimal bloodSugar = record.getBloodSugar();
        if (bloodSugar == null) {
            return;
        }

        if (bloodSugar.compareTo(new BigDecimal("10.0")) >= 0) {
            createWarning(record, "blood_sugar", "high", "Blood sugar is high");
            return;
        }
        if (bloodSugar.compareTo(new BigDecimal("7.0")) >= 0) {
            createWarning(record, "blood_sugar", "medium", "Blood sugar is above normal range");
        }
    }

    private void checkHeartRate(HealthRecord record) {
        Integer heartRate = record.getHeartRate();
        if (heartRate == null) {
            return;
        }

        if (heartRate >= 110 || heartRate <= 50) {
            createWarning(record, "heart_rate", "high", "Heart rate is abnormal");
            return;
        }
        if (heartRate >= 100 || heartRate <= 60) {
            createWarning(record, "heart_rate", "medium", "Heart rate is outside normal range");
        }
    }

    private void checkTemperature(HealthRecord record) {
        BigDecimal temperature = record.getTemperature();
        if (temperature == null) {
            return;
        }

        if (temperature.compareTo(new BigDecimal("38.0")) >= 0) {
            createWarning(record, "temperature", "high", "Temperature is high");
            return;
        }
        if (temperature.compareTo(new BigDecimal("37.3")) >= 0) {
            createWarning(record, "temperature", "medium", "Temperature is above normal range");
        }
    }

    private boolean greaterThanOrEqual(Integer value, int threshold) {
        return value != null && value >= threshold;
    }

    private boolean hasBloodPressure(HealthRecord record) {
        return record.getSystolicPressure() != null || record.getDiastolicPressure() != null;
    }

    private boolean isBloodPressureAbnormal(HealthRecord record) {
        return greaterThanOrEqual(record.getSystolicPressure(), 140)
            || greaterThanOrEqual(record.getDiastolicPressure(), 90);
    }

    private boolean isBloodSugarAbnormal(HealthRecord record) {
        return record.getBloodSugar().compareTo(new BigDecimal("7.0")) >= 0;
    }

    private boolean isHeartRateAbnormal(HealthRecord record) {
        Integer heartRate = record.getHeartRate();
        return heartRate >= 100 || heartRate <= 60;
    }

    private boolean isTemperatureAbnormal(HealthRecord record) {
        return record.getTemperature().compareTo(new BigDecimal("37.3")) >= 0;
    }

    private void createWarning(HealthRecord record, String warningType, String warningLevel, String warningContent) {
        HealthWarning warning = new HealthWarning();
        warning.setElderlyId(record.getElderlyId());
        warning.setHealthRecordId(record.getId());
        warning.setWarningType(warningType);
        warning.setWarningLevel(warningLevel);
        warning.setWarningContent(warningContent);
        warning.setStatus("pending");
        warning.setWarningTime(record.getRecordTime());
        healthWarningMapper.insert(warning);
    }

    private boolean hasMeasurementForWarningType(HealthRecord record, String warningType) {
        return switch (warningType) {
            case "blood_pressure" -> hasBloodPressure(record);
            case "blood_sugar" -> record.getBloodSugar() != null;
            case "heart_rate" -> record.getHeartRate() != null;
            case "temperature" -> record.getTemperature() != null;
            default -> false;
        };
    }

    private boolean isNormalForWarningType(HealthRecord record, String warningType) {
        return switch (warningType) {
            case "blood_pressure" -> !isBloodPressureAbnormal(record);
            case "blood_sugar" -> !isBloodSugarAbnormal(record);
            case "heart_rate" -> !isHeartRateAbnormal(record);
            case "temperature" -> !isTemperatureAbnormal(record);
            default -> false;
        };
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

    private int normalizeDays(Integer days) {
        if (days == null || days < 1) {
            return 7;
        }
        return Math.min(days, 365);
    }
}
