use boardpass;

drop table users;

CREATE TABLE IF NOT EXISTS users (
    uid INT(11) PRIMARY KEY AUTO_INCREMENT,
    user_type INT(1) DEFAULT 1,
    full_name VARCHAR(50) NOT NULL,
    birth_date VARCHAR(50) NOT NULL,
    id_card VARCHAR(10),
    vip_pref VARCHAR(50),
    email VARCHAR(100) NOT NULL UNIQUE,
    encrypted_password VARCHAR(255) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME NULL
);

ALTER TABLE users AUTO_INCREMENT = 1;