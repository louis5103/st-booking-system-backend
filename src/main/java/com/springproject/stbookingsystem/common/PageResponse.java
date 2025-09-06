package com.springproject.stbookingsystem.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.Page;

import java.util.List;

/**
 * 페이징 응답 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {
    
    private List<T> content;
    
    private int page;
    
    private int size;
    
    private long totalElements;
    
    private int totalPages;
    
    private boolean first;
    
    private boolean last;
    
    private boolean empty;
    
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
    
    public static <T> ApiResponse<PageResponse<T>> success(Page<T> page) {
        return ApiResponse.success(PageResponse.of(page));
    }
    
    public static <T> ApiResponse<PageResponse<T>> success(String message, Page<T> page) {
        return ApiResponse.success(message, PageResponse.of(page));
    }
}
