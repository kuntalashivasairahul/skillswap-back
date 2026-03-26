package com.skillswap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response model for AI skill recommendation endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIRecommendationResponse {

    /** Ordered list of recommended skill names for the target user. */
    private List<String> recommendedSkills;
}
