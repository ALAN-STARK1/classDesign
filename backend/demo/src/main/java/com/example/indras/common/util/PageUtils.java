package com.example.indras.common.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.indras.common.api.PageResult;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class PageUtils {

    private PageUtils() {
    }

    public static <E, V> PageResult<V> toPageResult(Page<E> page, Function<E, V> mapper) {
        List<V> items = page.getRecords().stream().map(mapper).collect(Collectors.toList());
        return PageResult.<V>builder()
                .items(items)
                .page((int) page.getCurrent())
                .size((int) page.getSize())
                .total(page.getTotal())
                .pages((int) page.getPages())
                .build();
    }
}
