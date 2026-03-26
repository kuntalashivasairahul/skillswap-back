package com.skillswap.model;

/**
 * Status of a match request between two users.
 */
public enum MatchStatus {
    PENDING,    // Request sent, awaiting response
    ACCEPTED,   // Both parties agreed to exchange skills
    REJECTED    // Request declined
}
