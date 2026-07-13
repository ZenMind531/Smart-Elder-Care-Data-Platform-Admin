package com.smarteldercare.modules.care.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.modules.care.dto.CareRecordDTO;
import com.smarteldercare.modules.care.entity.CareRecord;
import com.smarteldercare.modules.care.vo.CareRecordVO;

public interface CareRecordService extends IService<CareRecord> {

    PageResult<CareRecordVO> listCareRecords(Long page, Long size, Long elderlyId, String careType);

    CareRecordVO getCareRecord(Long id);

    CareRecordVO createCareRecord(CareRecordDTO dto);

    CareRecordVO updateCareRecord(Long id, CareRecordDTO dto);

    void deleteCareRecord(Long id);
}
