package com.recipemanager.common;

import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;

/** Paginated response envelope (TECH_SPEC §4.2). */
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    public static <E, T> PageResponse<T> from(Page<E> page, Function<? super E, ? extends T> mapper) {
        return from(page.map(mapper));
    }
}
