package com.skillswap.repository;

import com.skillswap.model.UserOfferedSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Data access layer for skills a user offers to teach.
 */
@Repository
public interface UserOfferedSkillRepository extends JpaRepository<UserOfferedSkill, Long> {

    /** All offered-skill records for a given user. */
    List<UserOfferedSkill> findByUserId(Long userId);

    /** All skill IDs offered by a given user (used in matchmaking). */
    @Query("SELECT uos.skill.id FROM UserOfferedSkill uos WHERE uos.user.id = :userId")
    Set<Long> findSkillIdsByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndSkillId(Long userId, Long skillId);
}
