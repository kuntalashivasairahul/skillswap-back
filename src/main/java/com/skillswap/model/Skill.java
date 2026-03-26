package com.skillswap.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * A skill entry in the platform's global skills catalog.
 * Users can associate skills as "offered" or "needed".
 */
@Entity
@Table(name = "skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Short unique name of the skill, e.g. "Python", "Guitar". */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /** Optional longer description of what the skill entails. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Category grouping, e.g. "Programming", "Music", "Language". */
    @Column(length = 100)
    private String category;
}
