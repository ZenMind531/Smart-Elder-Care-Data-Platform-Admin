package com.smarteldercare.modules.elderly.vo;

import lombok.Data;

@Data
public class ElderlyImportResultVO {
    private int successCount;
    private int failCount;
    private StringBuilder failedMessages = new StringBuilder();

    public void addSuccess() {
        this.successCount++;
    }

    public void addFailure(String msg) {
        this.failCount++;
        if (failedMessages.length() > 0) failedMessages.append("; ");
        failedMessages.append(msg);
    }

    public String getMessage() {
        return "成功" + successCount + "条，失败" + failCount + "条" +
            (failCount > 0 ? "：" + failedMessages.toString() : "");
    }
}
