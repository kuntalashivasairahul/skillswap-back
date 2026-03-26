package com.skillswap.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * A star rating (1–5) and optional written feedback left by one user for another
 * after a completed session.
 * The rated user's average rating on the User entity is updated when a Rating is submitted.
 */
@Entity
@Table(name = "ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** User who is submitting the rating. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** User who is being rated. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rated_user_id", nullable = false)
    private User ratedUser;

    /** Numeric score from 1 (poor) to 5 (excellent). */
    @Column(nullable = false)
    private Integer rating;

    /** Optional written feedback explaining the rating. */
    @Column(columnDefinition = "TEXT")
    private String feedback;
}
