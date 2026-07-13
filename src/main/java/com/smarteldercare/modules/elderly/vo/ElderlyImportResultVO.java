package com.smarteldercare.modules.elderly.vo;

import lombok.Data;

@Data
public class ElderlyImportResultVO {
    private int successCount;
    private int failCount;
    private String message;
}
