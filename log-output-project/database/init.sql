BEGIN;

CREATE TABLE counter (id serial PRIMARY KEY, counter INT);

INSERT INTO counter (counter) VALUES(0);

COMMIT;