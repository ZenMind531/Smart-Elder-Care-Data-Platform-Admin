package com.smarteldercare.modules.care.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smarteldercare.common.exception.BusinessException;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.common.result.ResultCode;
import com.smarteldercare.modules.care.dto.CareRecordDTO;
import com.smarteldercare.modules.care.entity.CareRecord;
import com.smarteldercare.modules.care.mapper.CareRecordMapper;
import com.smarteldercare.modules.care.service.CareRecordService;
import com.smarteldercare.modules.care.vo.CareRecordVO;
import com.smarteldercare.modules.elderly.mapper.ElderlyProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CareRecordServiceImpl
        extends ServiceImpl<CareRecordMapper, CareRecord>
        implements CareRecordService {

    private final ElderlyProfileMapper elderlyProfileMapper;

    @Override
    public PageResult<CareRecordVO> listCareRecords(Long page, Long size, Long elderlyId, String careType) {
        Page<CareRecord> p = new Page<>(
                page == null || page < 1 ? 1 : page,
                size == null || size < 1 ? 10 : Math.min(size, 100)
        );

        LambdaQueryWrapper<CareRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(elderlyId != null, CareRecord::getElderlyId, elderlyId)
               .eq(careType != null && !careType.isEmpty(), CareRecord::getCareType, careType)
               .orderByDesc(CareRecord::getCareTime);

        Page<CareRecord> result = this.page(p, wrapper);
        List<CareRecordVO> records = result.getRecords().stream()
                .map(this::toVO)
                .toList();
        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public CareRecordVO getCareRecord(Long id) {
        return toVO(getExistingById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CareRecordVO createCareRecord(CareRecordDTO dto) {
        if (elderlyProfileMapper.selectById(dto.getElderlyId()) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        CareRecord record = new CareRecord();
        BeanUtils.copyProperties(dto, record);
        save(record);
        return toVO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CareRecordVO updateCareRecord(Long id, CareRecordDTO dto) {
        CareRecord record = getExistingById(id);
        BeanUtils.copyProperties(dto, record);
        record.setId(id);
        updateById(record);
        return toVO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCareRecord(Long id) {
        getExistingById(id);
        removeById(id);
    }

    private CareRecord getExistingById(Long id) {
        CareRecord record = getById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return record;
    }

    private CareRecordVO toVO(CareRecord record) {
        CareRecordVO vo = new CareRecordVO();
        BeanUtils.copyProperties(record, vo);
        if (record.getElderlyId() != null) {
            com.smarteldercare.modules.elderly.entity.ElderlyProfile e = elderlyProfileMapper.selectById(record.getElderlyId());
            if (e != null) vo.setElderlyName(e.getElderlyName());
        }
        return vo;
    }
}
