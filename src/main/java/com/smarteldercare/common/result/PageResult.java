package com.smarteldercare.common.result;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private List<T> records;
    private Long total;
    private Long page;
    private Long size;

    public static <T> PageResult<T> empty(Long page, Long size) {
        return new PageResult<>(Collections.emptyList(), 0L, page, size);
    }
}
