# --- First database schema

# --- !Ups

CREATE TABLE passwords (
  id                        bigint NOT NULL AUTO_INCREMENT,
  user_id                   varchar(255) not null,
  folder                    varchar(255) not null,
  data                      text,
  PRIMARY KEY (id),
  INDEX idx_user_id (user_id),
  INDEX idx_user_id_folder (user_id, folder)
);

# --- !Downs

DROP TABLE if EXISTS passwords;