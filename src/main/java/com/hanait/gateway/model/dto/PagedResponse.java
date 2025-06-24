package com.hanait.gateway.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponse<T> {
    private List<T> items;
    private Pagination pagination;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Pagination {
        private int currentPage;
        private int totalPages;
        private long totalElements;
        private int pageSize;
        private boolean isLast;
    }

    public static <T> PagedResponse<T> from(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                new Pagination(
                        page.getNumber(),
                        page.getTotalPages(),
                        page.getTotalElements(),
                        page.getSize(),
                        page.isLast()
                )
        );
    }
}