package com.skillswap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Read-model returned when listing skills.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillResponse {

    private Long   id;
    private String name;
    private String description;
    private String category;
}
