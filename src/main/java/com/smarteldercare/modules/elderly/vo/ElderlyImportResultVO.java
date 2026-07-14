package com.smarteldercare.modules.elderly.vo;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ElderlyImportResultVO {
    private int successCount;
    private int failCount;
    private String message;
    private List<String> errors = new ArrayList<>();

    public void addSuccess() {
        successCount++;
        refreshMessage();
    }

    public void addFailure(String error) {
        failCount++;
        errors.add(error);
        refreshMessage();
    }

    private void refreshMessage() {
        message = "导入成功 " + successCount + " 条，失败 " + failCount + " 条";
    }
}
