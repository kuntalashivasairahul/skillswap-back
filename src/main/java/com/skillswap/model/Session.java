package com.skillswap.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A scheduled skill-exchange session between two matched users.
 * Sessions can only be booked when the associated Match is ACCEPTED.
 */
@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The accepted match this session belongs to. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    /** Agreed date and time for the session. */
    @Column(name = "session_date", nullable = false)
    private LocalDateTime sessionDate;

    /** Lifecycle status of this session. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SessionStatus status = SessionStatus.SCHEDULED;
}
