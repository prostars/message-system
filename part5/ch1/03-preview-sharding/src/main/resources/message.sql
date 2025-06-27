CREATE TABLE IF NOT EXISTS message (
    message_sequence BIGINT AUTO_INCREMENT,
    channel_id BIGINT NOT NULL,
    user_name VARCHAR(20) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL,    
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (message_sequence)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
