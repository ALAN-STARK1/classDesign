package com.example.indras.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private List<T> items;
    private int page;
    private int size;
    private long total;
    private int pages;

    public static <T> PageResult<T> empty(int page, int size) {
        return PageResult.<T>builder()
                .items(Collections.emptyList())
                .page(page)
                .size(size)
                .total(0)
                .pages(0)
                .build();
    }
}
