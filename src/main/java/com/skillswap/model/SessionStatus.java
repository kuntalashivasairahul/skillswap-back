package com.skillswap.model;

/**
 * Status of a booked skill-exchange session.
 */
public enum SessionStatus {
    SCHEDULED,  // Session is booked and upcoming
    COMPLETED,  // Session took place
    CANCELLED   // Session was cancelled before it happened
}
