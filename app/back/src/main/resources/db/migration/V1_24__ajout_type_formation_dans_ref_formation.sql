ALTER TABLE ref_formation ADD COLUMN type_formation varchar(255);

CREATE TABLE sugg_config (
    id uuid NOT NULL,
    active boolean NOT NULL,
    config jsonb NOT NULL,
    description varchar(255) NOT NULL,
    PRIMARY KEY(id)
);
