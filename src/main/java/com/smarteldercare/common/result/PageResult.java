package com.smarteldercare.common.result;

import java.util.List;

public class PageResult<T> {
    private List<T> records;  // 这一页的数据
    private Long total;       // 总共多少条
    private Long page;        // 当前第几页
    private Long size;        // 每页多少条

    public PageResult() {
    }

    public PageResult(List<T> records, Long total, Long page, Long size) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public List<T> getRecords() { return records; }
    public void setRecords(List<T> records) { this.records = records; }
    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
    public Long getPage() { return page; }
    public void setPage(Long page) { this.page = page; }
    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }
}