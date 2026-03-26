package com.skillswap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Payload for creating a new skill in the global catalog.
 */
@Data
public class SkillRequest {

    @NotBlank(message = "Skill name is required")
    @Size(max = 100)
    private String name;

    private String description;

    @Size(max = 100)
    private String category;
}
