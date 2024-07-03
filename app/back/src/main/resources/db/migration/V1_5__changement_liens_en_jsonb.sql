ALTER TABLE formation
    DROP COLUMN liens;

ALTER TABLE formation
    ADD COLUMN liens jsonb;

ALTER TABLE metier
    DROP COLUMN liens;

ALTER TABLE metier
    ADD COLUMN liens jsonb;