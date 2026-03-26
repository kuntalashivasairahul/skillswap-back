package com.skillswap.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Represents a match between two users (userA and userB) who have
 * complementary skills — each offers what the other needs.
 *
 * matchScore = (skillOverlap × 2) + candidateRating
 */
@Entity
@Table(
    name = "matches",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_a_id", "user_b_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who initiates the match request. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_a_id", nullable = false)
    private User userA;

    /** The user who receives the match request. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_b_id", nullable = false)
    private User userB;

    /** Current status of the match (PENDING / ACCEPTED / REJECTED). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MatchStatus status = MatchStatus.PENDING;

    /**
     * Calculated score at the time the match was created.
     * Formula: (number of mutually matched skills × 2) + candidate.rating
     */
    @Column(name = "match_score", nullable = false)
    @Builder.Default
    private Double matchScore = 0.0;

    /** Timestamp when the match record was created. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
