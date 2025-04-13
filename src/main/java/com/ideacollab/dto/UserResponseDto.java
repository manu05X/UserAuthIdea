package com.ideacollab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String employeeId;
    private String name;
    private int createdIdeasCount;
    private int participatedIdeasCount;
}
