package com.skillswap.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Payload for booking a new session for an accepted match.
 */
@Data
public class SessionRequest {

    @NotNull(message = "Match ID is required")
    private Long matchId;

    @NotNull(message = "Session date is required")
    private LocalDateTime sessionDate;
}
