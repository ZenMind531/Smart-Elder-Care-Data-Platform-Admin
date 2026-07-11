package com.smarteldercare.modules.health.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.modules.health.dto.HealthRecordDTO;
import com.smarteldercare.modules.health.dto.HealthRecordStatusDTO;
import com.smarteldercare.modules.health.entity.HealthRecord;
import com.smarteldercare.modules.health.vo.HealthRecordTrendVO;
import com.smarteldercare.modules.health.vo.HealthRecordVO;
import java.util.List;

public interface HealthRecordService extends IService<HealthRecord> {

    PageResult<HealthRecordVO> listHealthRecords(Long page, Long size, Long elderlyId);

    List<HealthRecordTrendVO> listHealthRecordTrend(Long elderlyId, Integer days);

    HealthRecordVO getHealthRecord(Long id);

    HealthRecordVO createHealthRecord(HealthRecordDTO dto);

    void updateHealthRecordStatus(Long id, HealthRecordStatusDTO dto);

    void deleteHealthRecord(Long id);
}
