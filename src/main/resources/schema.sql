-- ============================================================
--  SkillSwap – Reference Database Schema
--  Hibernate generates the tables automatically (ddl-auto=update)
--  This file is kept as documentation and can be run manually.
-- ============================================================

CREATE DATABASE IF NOT EXISTS skillswap_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE skillswap_db;

-- ── Users ────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT          NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100)    NOT NULL,
    email      VARCHAR(150)    NOT NULL UNIQUE,
    password   VARCHAR(255)    NOT NULL,           -- BCrypt hash
    bio        TEXT,
    rating     DOUBLE          NOT NULL DEFAULT 0.0,
    created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_users_email (email)
);

-- ── Skills catalog ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS skills (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100)    NOT NULL UNIQUE,
    description TEXT,
    category    VARCHAR(100),
    PRIMARY KEY (id),
    INDEX idx_skills_name (name)
);

-- ── Skills a user offers to teach ────────────────────────────
CREATE TABLE IF NOT EXISTS user_offered_skills (
    id       BIGINT NOT NULL AUTO_INCREMENT,
    user_id  BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_offered (user_id, skill_id),
    FOREIGN KEY (user_id)  REFERENCES users(id)  ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
);

-- ── Skills a user wants to learn ─────────────────────────────
CREATE TABLE IF NOT EXISTS user_needed_skills (
    id       BIGINT NOT NULL AUTO_INCREMENT,
    user_id  BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_needed (user_id, skill_id),
    FOREIGN KEY (user_id)  REFERENCES users(id)  ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
);

-- ── Matches between two users ─────────────────────────────────
CREATE TABLE IF NOT EXISTS matches (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    user_a_id   BIGINT          NOT NULL,
    user_b_id   BIGINT          NOT NULL,
    status      ENUM('PENDING','ACCEPTED','REJECTED') NOT NULL DEFAULT 'PENDING',
    match_score DOUBLE          NOT NULL DEFAULT 0.0,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_match_pair (user_a_id, user_b_id),
    FOREIGN KEY (user_a_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (user_b_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_matches_user_a (user_a_id),
    INDEX idx_matches_user_b (user_b_id)
);

-- ── Scheduled sessions ────────────────────────────────────────
CREATE TABLE IF NOT EXISTS sessions (
    id           BIGINT          NOT NULL AUTO_INCREMENT,
    match_id     BIGINT          NOT NULL,
    session_date DATETIME        NOT NULL,
    status       ENUM('SCHEDULED','COMPLETED','CANCELLED') NOT NULL DEFAULT 'SCHEDULED',
    PRIMARY KEY (id),
    FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    INDEX idx_sessions_match (match_id)
);

-- ── Messages between users ────────────────────────────────────
CREATE TABLE IF NOT EXISTS messages (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    sender_id   BIGINT          NOT NULL,
    receiver_id BIGINT          NOT NULL,
    message     TEXT            NOT NULL,
    timestamp   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (sender_id)   REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_messages_sender   (sender_id),
    INDEX idx_messages_receiver (receiver_id)
);

-- ── Ratings after sessions ────────────────────────────────────
CREATE TABLE IF NOT EXISTS ratings (
    id            BIGINT          NOT NULL AUTO_INCREMENT,
    user_id       BIGINT          NOT NULL,           -- who gives the rating
    rated_user_id BIGINT          NOT NULL,           -- who receives it
    rating        INT             NOT NULL CHECK (rating BETWEEN 1 AND 5),
    feedback      TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id)       REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (rated_user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_ratings_rated_user (rated_user_id)
);
