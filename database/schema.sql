CREATE DATABASE IF NOT EXISTS library_sync
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_sync;


CREATE TABLE IF NOT EXISTS books (
  id                 BIGINT        NOT NULL AUTO_INCREMENT,
  external_id        VARCHAR(50)   NOT NULL,         
  title              VARCHAR(500)  NOT NULL,
  authors            VARCHAR(1000) NOT NULL DEFAULT '',
  first_publish_year INT           NULL,
  data_hash          CHAR(64)      NOT NULL,           
  sync_status        VARCHAR(20)   NOT NULL,           
  last_checked_at    DATETIME(3)   NOT NULL,
  last_changed_at    DATETIME(3)   NOT NULL,
  created_at         DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  CONSTRAINT uq_books_external_id UNIQUE (external_id),
  INDEX idx_books_status (sync_status),
  INDEX idx_books_title (title(100))
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS sync_runs (
  id              BIGINT        NOT NULL AUTO_INCREMENT,
  triggered_by    VARCHAR(20)   NOT NULL,             
  status          VARCHAR(20)   NOT NULL,              
  started_at      DATETIME(3)   NOT NULL,
  finished_at     DATETIME(3)   NULL,
  fetched_count   INT           NOT NULL DEFAULT 0,
  new_count       INT           NOT NULL DEFAULT 0,
  updated_count   INT           NOT NULL DEFAULT 0,
  unchanged_count INT           NOT NULL DEFAULT 0,
  error_message   VARCHAR(1000) NULL,
  PRIMARY KEY (id),
  INDEX idx_sync_runs_started (started_at)
) ENGINE=InnoDB;