package com.smarteldercare.modules.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smarteldercare.common.exception.BusinessException;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.common.result.ResultCode;
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
        if (elderlyProfileMapper.selectById(dto.getElderlyId()) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        HealthRecord healthRecord = new HealthRecord();
        BeanUtils.copyProperties(dto, healthRecord);
        save(healthRecord);
        checkHealthRecordAndCreateWarning(healthRecord);
        healthRiskLevelService.recalculateRiskLevel(healthRecord.getElderlyId());
        return toVO(healthRecord);
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

    private void checkBloodPressure(HealthRecord record) {
        Integer systolicPressure = record.getSystolicPressure();
        Integer diastolicPressure = record.getDiastolicPressure();
        if (systolicPressure == null && diastolicPressure == null) {
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

    private void createWarning(HealthRecord record, String warningType, String warningLevel, String warningContent) {
        HealthWarning warning = new HealthWarning();
        warning.setElderlyId(record.getElderlyId());
        warning.setWarningType(warningType);
        warning.setWarningLevel(warningLevel);
        warning.setWarningContent(warningContent);
        warning.setStatus("pending");
        warning.setWarningTime(record.getRecordTime());
        healthWarningMapper.insert(warning);
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
