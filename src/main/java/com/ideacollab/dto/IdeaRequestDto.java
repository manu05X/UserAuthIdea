package com.ideacollab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaRequestDto {
    private String title;
    private String description;
    private Set<Long> tagIds;
}
