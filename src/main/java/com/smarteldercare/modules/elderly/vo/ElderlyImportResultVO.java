package com.smarteldercare.modules.elderly.vo;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ElderlyImportResultVO {

    private int totalCount;

    private int successCount;

    private int failureCount;

    private List<String> errors = new ArrayList<>();

    public void addSuccess() {
        this.successCount++;
        this.totalCount++;
    }

    public void addFailure(String error) {
        this.failureCount++;
        this.totalCount++;
        this.errors.add(error);
    }
}
