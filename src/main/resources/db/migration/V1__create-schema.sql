CREATE TABLE application (
  application_id      VARCHAR(128) NOT NULL PRIMARY KEY,
  application_name    VARCHAR(128) NOT NULL,
  url                 VARCHAR(255) NOT NULL,
  read_sensitive_data BOOLEAN      NOT NULL DEFAULT TRUE
)
  ENGINE = InnoDB;

CREATE TABLE access_token (
  application_id VARCHAR(128) NOT NULL PRIMARY KEY,
  token          TEXT         NOT NULL,
  FOREIGN KEY (application_id) REFERENCES application (application_id)
    ON DELETE CASCADE
)
  ENGINE = InnoDB;