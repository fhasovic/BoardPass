use boardpass;

drop table myevents;

CREATE TABLE IF NOT EXISTS myevents (
    uid INT(11) PRIMARY KEY AUTO_INCREMENT,
    event_name VARCHAR(100) NOT NULL,
    place_name VARCHAR(50) NOT NULL,
    event_date DATE NOT NULL,
    event_time TIME NOT NULL,
    address VARCHAR(50) NOT NULL,
    price VARCHAR(10) NOT NULL,
    picture VARCHAR(400) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME NULL
);

ALTER TABLE myevents AUTO_INCREMENT = 1;