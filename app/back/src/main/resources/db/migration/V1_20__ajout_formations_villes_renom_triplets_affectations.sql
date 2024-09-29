ALTER TABLE ref_triplet_affectation RENAME TO ref_voeu;

CREATE TABLE ref_join_ville_voeu (
    id_ville varchar(255) NOT NULL,
    voeux_10km character varying[] NOT NULL,
    voeux_30km character varying[] NOT NULL
);

ALTER TABLE ref_formation RENAME COLUMN formations_associees to formations_psup;
