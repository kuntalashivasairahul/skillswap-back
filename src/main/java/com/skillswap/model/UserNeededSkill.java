package com.skillswap.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Junction entity linking a User to a Skill they want to LEARN.
 */
@Entity
@Table(
    name = "user_needed_skills",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "skill_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNeededSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who needs this skill. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** The skill being sought. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
}
