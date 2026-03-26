package com.skillswap.repository;

import com.skillswap.model.Match;
import com.skillswap.model.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data access layer for {@link Match} records.
 */
@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    /**
     * Returns all matches where the given user is either the requester (userA)
     * or the recipient (userB).
     */
    @Query("SELECT m FROM Match m WHERE m.userA.id = :userId OR m.userB.id = :userId")
    List<Match> findByUserId(@Param("userId") Long userId);

    /**
     * Returns matches with a specific status involving the given user.
     */
    @Query("SELECT m FROM Match m WHERE (m.userA.id = :userId OR m.userB.id = :userId) AND m.status = :status")
    List<Match> findByUserIdAndStatus(@Param("userId") Long userId,
                                      @Param("status") MatchStatus status);

    /**
     * Check whether a match pair already exists (either direction).
     */
    @Query("SELECT COUNT(m) > 0 FROM Match m WHERE (m.userA.id = :aId AND m.userB.id = :bId) OR (m.userA.id = :bId AND m.userB.id = :aId)")
    boolean existsByUserPair(@Param("aId") Long aId, @Param("bId") Long bId);
}
