ALTER TABLE ref_triplet_affectation RENAME TO ref_voeux;

CREATE TABLE ref_ville_voeux (
    id_ville varchar(255) NOT NULL,
    voeux_10km character varying[] NOT NULL,
    voeux_30km character varying[] NOT NULL
);
