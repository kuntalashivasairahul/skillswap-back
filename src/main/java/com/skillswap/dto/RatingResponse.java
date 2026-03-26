package com.skillswap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Read-model for a rating record, returned in API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {

    private Long         id;
    private UserResponse user;        // who gave the rating
    private UserResponse ratedUser;   // who received it
    private Integer      rating;
    private String       feedback;
}
