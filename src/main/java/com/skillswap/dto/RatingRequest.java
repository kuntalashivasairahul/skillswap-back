package com.skillswap.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Payload for submitting a rating for another user after a session.
 */
@Data
public class RatingRequest {

    @NotNull(message = "Rater user ID is required")
    private Long userId;

    @NotNull(message = "Rated user ID is required")
    private Long ratedUserId;

    @NotNull(message = "Rating value is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer rating;

    /** Optional written feedback accompanying the numeric rating. */
    private String feedback;
}
