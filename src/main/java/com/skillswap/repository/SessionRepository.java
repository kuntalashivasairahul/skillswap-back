package com.skillswap.repository;

import com.skillswap.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data access layer for {@link Session} records.
 */
@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    /** All sessions for a given match. */
    List<Session> findByMatchId(Long matchId);

    /**
     * All sessions where the given user is either userA or userB of the underlying match —
     * used to fetch a user's full session calendar.
     */
    @Query("SELECT s FROM Session s WHERE s.match.userA.id = :userId OR s.match.userB.id = :userId")
    List<Session> findByUserId(@Param("userId") Long userId);
}
