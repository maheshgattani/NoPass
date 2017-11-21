# --- First database schema

# --- !Ups

CREATE TABLE accounts (
  id                        bigint NOT NULL AUTO_INCREMENT,
  first_name                varchar(255) not null,
  last_name                 varchar(255) not null,
  email                     varchar(255) not null,
  verified                  tinyint(1) not null default 0,
  password                  varchar(255) not null,
  PRIMARY KEY (id),
  CONSTRAINT uk_email UNIQUE (email)
);

# --- !Downs

DROP TABLE if EXISTS accounts;