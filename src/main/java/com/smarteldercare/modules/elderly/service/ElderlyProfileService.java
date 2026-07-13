package com.smarteldercare.modules.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.modules.elderly.dto.ElderlyProfileDTO;
import com.smarteldercare.modules.elderly.entity.ElderlyProfile;
import com.smarteldercare.modules.elderly.vo.ElderlyHealthSummaryVO;
import com.smarteldercare.modules.elderly.vo.ElderlyImportResultVO;
import com.smarteldercare.modules.elderly.vo.ElderlyProfileVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ElderlyProfileService extends IService<ElderlyProfile> {

    PageResult<ElderlyProfileVO> listElderlyProfiles(Long page, Long size, String keyword, String gender);

    ElderlyProfileVO getElderlyProfile(Long id);

    ElderlyHealthSummaryVO getHealthSummary(Long id);

    ElderlyProfileVO createElderlyProfile(ElderlyProfileDTO dto);

    ElderlyProfileVO updateElderlyProfile(Long id, ElderlyProfileDTO dto);

    void deleteElderlyProfile(Long id);

    void exportElderlyProfiles(HttpServletResponse response, String keyword, String gender);

    ElderlyImportResultVO importElderlyProfiles(MultipartFile file);
}
