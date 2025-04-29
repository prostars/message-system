CREATE TABLE IF NOT EXISTS message (
    message_sequence BIGINT AUTO_INCREMENT,
    user_name VARCHAR(20) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL,    
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (message_sequence)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS message_user (
    user_id BIGINT AUTO_INCREMENT,
    username VARCHAR(20) NOT NULL,
    password VARCHAR(255) NOT NULL,
    connection_invite_code VARCHAR(32) NOT NULL,
    connection_count INT NOT NULL,
    created_at TIMESTAMP NOT NULL,    
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id),
    CONSTRAINT unique_username UNIQUE (username),
    CONSTRAINT unique_connection_invite_code UNIQUE (connection_invite_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS user_connection (
    partner_a_user_id BIGINT NOT NULL,
    partner_b_user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    inviter_user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,    
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (partner_a_user_id, partner_b_user_id),
    INDEX idx_partner_b_user_id (partner_b_user_id),
    INDEX idx_partner_a_user_id_status (partner_a_user_id, status),
    INDEX idx_partner_b_user_id_status (partner_b_user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS channel (
    channel_id BIGINT AUTO_INCREMENT,
    title VARCHAR(30) NOT NULL,
    channel_invite_code VARCHAR(32) NOT NULL,
    head_count INT NOT NULL,
    created_at TIMESTAMP NOT NULL,    
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (channel_id),
    CONSTRAINT unique_channel_invite_code UNIQUE (channel_invite_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS user_channel (
    user_id BIGINT NOT NULL,
    channel_id BIGINT NOT NULL,
    last_read_msg_seq BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,    
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id, channel_id),
    INDEX idx_channel_id (channel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
