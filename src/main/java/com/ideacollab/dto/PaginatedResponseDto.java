package com.ideacollab.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponseDto<T> {
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalItems;
}

