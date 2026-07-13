package com.smarteldercare.modules.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smarteldercare.common.exception.BusinessException;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.common.result.ResultCode;
import com.smarteldercare.modules.elderly.dto.ElderlyProfileDTO;
import com.smarteldercare.modules.elderly.entity.ElderlyProfile;
import com.smarteldercare.modules.elderly.mapper.ElderlyProfileMapper;
import com.smarteldercare.modules.elderly.service.ElderlyProfileService;
import com.smarteldercare.modules.elderly.vo.ElderlyHealthSummaryVO;
import com.smarteldercare.modules.elderly.vo.ElderlyImportResultVO;
import com.smarteldercare.modules.elderly.vo.ElderlyProfileVO;
import com.smarteldercare.modules.health.entity.HealthRecord;
import com.smarteldercare.modules.health.entity.HealthWarning;
import com.smarteldercare.modules.health.mapper.HealthRecordMapper;
import com.smarteldercare.modules.health.mapper.HealthWarningMapper;
import com.smarteldercare.modules.health.vo.HealthRecordVO;
import com.smarteldercare.modules.health.vo.HealthWarningVO;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ElderlyProfileServiceImpl
    extends ServiceImpl<ElderlyProfileMapper, ElderlyProfile>
    implements ElderlyProfileService {

    private final HealthRecordMapper healthRecordMapper;
    private final HealthWarningMapper healthWarningMapper;

    @Override
    public PageResult<ElderlyProfileVO> listElderlyProfiles(Long page, Long size, String keyword, String gender) {
        Long currentPage = normalizePage(page);
        Long pageSize = normalizeSize(size);

        LambdaQueryWrapper<ElderlyProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), ElderlyProfile::getElderlyName, keyword)
            .eq(StringUtils.hasText(gender), ElderlyProfile::getGender, gender)
            .orderByDesc(ElderlyProfile::getCreateTime);

        Page<ElderlyProfile> result = page(new Page<>(currentPage, pageSize), wrapper);
        List<ElderlyProfileVO> records = result.getRecords().stream()
            .map(this::toVO)
            .toList();
        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public ElderlyProfileVO getElderlyProfile(Long id) {
        return toVO(getExistingById(id));
    }

    @Override
    public ElderlyHealthSummaryVO getHealthSummary(Long id) {
        ElderlyProfile elderlyProfile = getExistingById(id);

        ElderlyHealthSummaryVO summary = new ElderlyHealthSummaryVO();
        summary.setElderlyInfo(toVO(elderlyProfile));
        summary.setLatestHealthRecord(getLatestHealthRecord(id));
        summary.setRecentWarnings(getRecentWarnings(id, elderlyProfile.getElderlyName()));
        summary.setRiskLevel(elderlyProfile.getRiskLevel());
        summary.setPendingWarningCount(countPendingWarnings(id));
        return summary;
    }

    @Override
    public ElderlyProfileVO createElderlyProfile(ElderlyProfileDTO dto) {
        ElderlyProfile elderlyProfile = new ElderlyProfile();
        BeanUtils.copyProperties(dto, elderlyProfile);
        if (!StringUtils.hasText(elderlyProfile.getRiskLevel())) {
            elderlyProfile.setRiskLevel("low");
        }
        save(elderlyProfile);
        return toVO(elderlyProfile);
    }

    @Override
    public ElderlyProfileVO updateElderlyProfile(Long id, ElderlyProfileDTO dto) {
        ElderlyProfile elderlyProfile = getExistingById(id);
        BeanUtils.copyProperties(dto, elderlyProfile);
        if (!StringUtils.hasText(elderlyProfile.getRiskLevel())) {
            elderlyProfile.setRiskLevel("low");
        }
        updateById(elderlyProfile);
        return toVO(getExistingById(id));
    }

    @Override
    public void deleteElderlyProfile(Long id) {
        getExistingById(id);
        removeById(id);
    }

    @Override
    public void exportElderlyProfiles(HttpServletResponse response, String keyword, String gender) {
        LambdaQueryWrapper<ElderlyProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), ElderlyProfile::getElderlyName, keyword)
            .eq(StringUtils.hasText(gender), ElderlyProfile::getGender, gender)
            .orderByDesc(ElderlyProfile::getCreateTime);
        List<ElderlyProfile> records = list(wrapper);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("病人列表");
            writeHeader(workbook, sheet);
            for (int i = 0; i < records.size(); i++) {
                writeElderlyRow(sheet.createRow(i + 1), records.get(i));
            }
            for (int i = 0; i < EXCEL_HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            String fileName = URLEncoder.encode("病人列表.xlsx", StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            workbook.write(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new BusinessException("导出病人列表失败");
        }
    }

    @Override
    public ElderlyImportResultVO importElderlyProfiles(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".xlsx")) {
            throw new BusinessException("请上传 xlsx 格式的 Excel 文件");
        }

        ElderlyImportResultVO result = new ElderlyImportResultVO();
        DataFormatter formatter = new DataFormatter();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (isBlankRow(row, formatter)) {
                    continue;
                }
                try {
                    ElderlyProfile elderlyProfile = parseElderlyProfile(row, formatter);
                    saveOrUpdateByIdCard(elderlyProfile);
                    result.addSuccess();
                } catch (Exception e) {
                    result.addFailure("第 " + (rowIndex + 1) + " 行：" + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new BusinessException("读取 Excel 文件失败");
        }
        return result;
    }

    private ElderlyProfile getExistingById(Long id) {
        ElderlyProfile elderlyProfile = getById(id);
        if (elderlyProfile == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return elderlyProfile;
    }

    private ElderlyProfileVO toVO(ElderlyProfile elderlyProfile) {
        ElderlyProfileVO vo = new ElderlyProfileVO();
        BeanUtils.copyProperties(elderlyProfile, vo);
        return vo;
    }

    private HealthRecordVO getLatestHealthRecord(Long elderlyId) {
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecord::getElderlyId, elderlyId)
            .orderByDesc(HealthRecord::getRecordTime)
            .orderByDesc(HealthRecord::getCreateTime)
            .last("LIMIT 1");
        HealthRecord healthRecord = healthRecordMapper.selectOne(wrapper);
        if (healthRecord == null) {
            return null;
        }

        HealthRecordVO vo = new HealthRecordVO();
        BeanUtils.copyProperties(healthRecord, vo);
        return vo;
    }

    private List<HealthWarningVO> getRecentWarnings(Long elderlyId, String elderlyName) {
        LambdaQueryWrapper<HealthWarning> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthWarning::getElderlyId, elderlyId)
            .orderByDesc(HealthWarning::getWarningTime)
            .orderByDesc(HealthWarning::getCreateTime)
            .last("LIMIT 3");
        return healthWarningMapper.selectList(wrapper).stream()
            .map(healthWarning -> toWarningVO(healthWarning, elderlyName))
            .toList();
    }

    private HealthWarningVO toWarningVO(HealthWarning healthWarning, String elderlyName) {
        HealthWarningVO vo = new HealthWarningVO();
        BeanUtils.copyProperties(healthWarning, vo);
        vo.setElderlyName(elderlyName);
        return vo;
    }

    private Long countPendingWarnings(Long elderlyId) {
        LambdaQueryWrapper<HealthWarning> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthWarning::getElderlyId, elderlyId)
            .ne(HealthWarning::getStatus, "resolved");
        return healthWarningMapper.selectCount(wrapper);
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

    private static final String[] EXCEL_HEADERS = {
        "姓名", "性别", "年龄", "身份证号", "联系电话", "地址",
        "紧急联系人", "紧急联系电话", "既往病史", "过敏史", "风险等级"
    };

    private void writeHeader(Workbook workbook, Sheet sheet) {
        Row header = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        for (int i = 0; i < EXCEL_HEADERS.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(EXCEL_HEADERS[i]);
            cell.setCellStyle(style);
        }
    }

    private void writeElderlyRow(Row row, ElderlyProfile elderlyProfile) {
        row.createCell(0).setCellValue(nullToBlank(elderlyProfile.getElderlyName()));
        row.createCell(1).setCellValue(nullToBlank(elderlyProfile.getGender()));
        row.createCell(2).setCellValue(elderlyProfile.getAge() == null ? "" : elderlyProfile.getAge().toString());
        row.createCell(3).setCellValue(nullToBlank(elderlyProfile.getIdCard()));
        row.createCell(4).setCellValue(nullToBlank(elderlyProfile.getPhoneNumber()));
        row.createCell(5).setCellValue(nullToBlank(elderlyProfile.getAddress()));
        row.createCell(6).setCellValue(nullToBlank(elderlyProfile.getEmergencyContact()));
        row.createCell(7).setCellValue(nullToBlank(elderlyProfile.getEmergencyPhone()));
        row.createCell(8).setCellValue(nullToBlank(elderlyProfile.getMedicalHistory()));
        row.createCell(9).setCellValue(nullToBlank(elderlyProfile.getAllergyHistory()));
        row.createCell(10).setCellValue(nullToBlank(elderlyProfile.getRiskLevel()));
    }

    private ElderlyProfile parseElderlyProfile(Row row, DataFormatter formatter) {
        ElderlyProfile elderlyProfile = new ElderlyProfile();
        elderlyProfile.setElderlyName(requiredCell(row, 0, formatter, "姓名"));
        elderlyProfile.setGender(requiredCell(row, 1, formatter, "性别"));
        elderlyProfile.setAge(parseAge(requiredCell(row, 2, formatter, "年龄")));
        elderlyProfile.setIdCard(cellValue(row, 3, formatter));
        elderlyProfile.setPhoneNumber(cellValue(row, 4, formatter));
        elderlyProfile.setAddress(cellValue(row, 5, formatter));
        elderlyProfile.setEmergencyContact(cellValue(row, 6, formatter));
        elderlyProfile.setEmergencyPhone(cellValue(row, 7, formatter));
        elderlyProfile.setMedicalHistory(cellValue(row, 8, formatter));
        elderlyProfile.setAllergyHistory(cellValue(row, 9, formatter));
        String riskLevel = cellValue(row, 10, formatter);
        elderlyProfile.setRiskLevel(StringUtils.hasText(riskLevel) ? riskLevel : "low");
        validateImportedElderly(elderlyProfile);
        return elderlyProfile;
    }

    private void saveOrUpdateByIdCard(ElderlyProfile elderlyProfile) {
        if (StringUtils.hasText(elderlyProfile.getIdCard())) {
            ElderlyProfile existing = lambdaQuery()
                .eq(ElderlyProfile::getIdCard, elderlyProfile.getIdCard())
                .one();
            if (existing != null) {
                elderlyProfile.setId(existing.getId());
                updateById(elderlyProfile);
                return;
            }
        }
        save(elderlyProfile);
    }

    private void validateImportedElderly(ElderlyProfile elderlyProfile) {
        if (!List.of("male", "female", "unknown").contains(elderlyProfile.getGender())) {
            throw new BusinessException("性别必须是 male、female 或 unknown");
        }
        Integer age = elderlyProfile.getAge();
        if (age == null || age < 0 || age > 130) {
            throw new BusinessException("年龄必须在 0 到 130 之间");
        }
        if (!List.of("low", "medium", "high").contains(elderlyProfile.getRiskLevel())) {
            throw new BusinessException("风险等级必须是 low、medium 或 high");
        }
    }

    private Integer parseAge(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new BusinessException("年龄必须是整数");
        }
    }

    private boolean isBlankRow(Row row, DataFormatter formatter) {
        if (row == null) {
            return true;
        }
        for (int i = 0; i < EXCEL_HEADERS.length; i++) {
            if (StringUtils.hasText(cellValue(row, i, formatter))) {
                return false;
            }
        }
        return true;
    }

    private String requiredCell(Row row, int index, DataFormatter formatter, String fieldName) {
        String value = cellValue(row, index, formatter);
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(fieldName + "不能为空");
        }
        return value;
    }

    private String cellValue(Row row, int index, DataFormatter formatter) {
        if (row == null) {
            return "";
        }
        return formatter.formatCellValue(row.getCell(index)).trim();
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }
}
