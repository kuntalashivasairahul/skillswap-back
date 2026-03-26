package com.skillswap.repository;

import com.skillswap.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data access layer for direct {@link Message} records.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Retrieve the full conversation thread between two users, ordered
     * chronologically.  We look up sender/receiver in both directions so
     * the full thread is always returned regardless of who initiated.
     */
    @Query("""
        SELECT m FROM Message m
        WHERE (m.sender.id = :userAId AND m.receiver.id = :userBId)
           OR (m.sender.id = :userBId AND m.receiver.id = :userAId)
        ORDER BY m.timestamp ASC
        """)
    List<Message> findConversation(@Param("userAId") Long userAId,
                                   @Param("userBId") Long userBId);
}
