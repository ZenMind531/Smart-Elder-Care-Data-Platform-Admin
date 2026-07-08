package com.smarteldercare.modules.elderly.vo;

import com.smarteldercare.modules.health.vo.HealthRecordVO;
import com.smarteldercare.modules.health.vo.HealthWarningVO;
import java.util.List;
import lombok.Data;

@Data
public class ElderlyHealthSummaryVO {

    private ElderlyProfileVO elderlyInfo;

    private HealthRecordVO latestHealthRecord;

    private List<HealthWarningVO> recentWarnings;

    private String riskLevel;

    private Long pendingWarningCount;
}
