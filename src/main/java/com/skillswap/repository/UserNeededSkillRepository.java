package com.skillswap.repository;

import com.skillswap.model.UserNeededSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Data access layer for skills a user wants to learn.
 */
@Repository
public interface UserNeededSkillRepository extends JpaRepository<UserNeededSkill, Long> {

    /** All needed-skill records for a given user. */
    List<UserNeededSkill> findByUserId(Long userId);

    /** All skill IDs needed by a given user (used in matchmaking). */
    @Query("SELECT uns.skill.id FROM UserNeededSkill uns WHERE uns.user.id = :userId")
    Set<Long> findSkillIdsByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndSkillId(Long userId, Long skillId);
}
