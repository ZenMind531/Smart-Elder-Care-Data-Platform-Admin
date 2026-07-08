package com.smarteldercare.modules.health.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.modules.health.dto.HealthWarningDTO;
import com.smarteldercare.modules.health.dto.HealthWarningStatusDTO;
import com.smarteldercare.modules.health.entity.HealthWarning;
import com.smarteldercare.modules.health.vo.HealthWarningVO;

public interface HealthWarningService extends IService<HealthWarning> {

    PageResult<HealthWarningVO> listHealthWarnings(
        Long page,
        Long size,
        Long elderlyId,
        String warningLevel,
        String status
    );

    HealthWarningVO getHealthWarning(Long id);

    HealthWarningVO createHealthWarning(HealthWarningDTO dto);

    HealthWarningVO updateHealthWarningStatus(Long id, HealthWarningStatusDTO dto);

    void deleteHealthWarning(Long id);
}
