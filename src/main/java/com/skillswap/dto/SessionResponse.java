package com.skillswap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Read-model for a session, returned in API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {

    private Long         id;
    private MatchResponse match;
    private String       sessionDate;
    private String       status;      // SCHEDULED / COMPLETED / CANCELLED
}
