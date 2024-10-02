DROP TABLE ref_join_ville_voeu;

CREATE TABLE ref_join_ville_voeu(
    id_ville varchar(255) NOT NULL,
    distances_voeux_km jsonb NOT NULL,
    PRIMARY KEY(id_ville)
);