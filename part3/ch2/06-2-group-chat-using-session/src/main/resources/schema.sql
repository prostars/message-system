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
    created_at TIMESTAMP NOT NULL,    
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id),
    CONSTRAINT unique_username UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
