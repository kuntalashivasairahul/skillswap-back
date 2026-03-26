package com.skillswap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Read-model for a match record, returned in API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {

    private Long         id;
    private UserResponse userA;
    private UserResponse userB;
    private String       status;      // PENDING / ACCEPTED / REJECTED
    private Double       matchScore;
    private String       createdAt;
}
