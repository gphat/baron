# --- !Ups

CREATE TABLE links (
  id INT UNSIGNED AUTO_INCREMENT NOT NULL,
  url TEXT NOT NULL,
  poster INT UNSIGNED,
  org INT UNSIGNED,
  position INT UNSIGNED,
  description TEXT,
  date_created DATETIME NOT NULL,
  PRIMARY KEY(id)
) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin;

# --- !Downs

DROP TABLE IF EXISTS links;