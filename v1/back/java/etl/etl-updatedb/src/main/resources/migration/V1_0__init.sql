CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS unaccent;

CREATE TABLE IF NOT EXISTS metier
(
    id                 VARCHAR(20) PRIMARY KEY,
    label              VARCHAR(300) NOT NULL,
    descriptif_general TEXT,
    urls               VARCHAR(3000)[]
);

CREATE TABLE IF NOT EXISTS formation
(
    id                     VARCHAR(20) PRIMARY KEY,
    label                  VARCHAR(300) NOT NULL,
    descriptif_general     TEXT,
    descriptif_specialites TEXT,
    descriptif_attendu     TEXT,
    mots_clefs             VARCHAR(300)[],
    urls                   VARCHAR(3000)[],
    formation_parente      VARCHAR(20) REFERENCES formation (id)
);

CREATE TABLE IF NOT EXISTS join_metier_formation
(
    metier_id    VARCHAR(20) NOT NULL,
    formation_id VARCHAR(20) NOT NULL,
    FOREIGN KEY (metier_id) REFERENCES metier (id),
    FOREIGN KEY (formation_id) REFERENCES formation (id),
    PRIMARY KEY (metier_id, formation_id)
);

CREATE TABLE IF NOT EXISTS triplet_affectation
(
    id VARCHAR(20) PRIMARY KEY,
    nom TEXT NOT NULL,
    commune VARCHAR(85) NOT NULL,
    code_commune VARCHAR(5) NOT NULL,
    coordonnees_geographiques FLOAT[2] NOT NULL,
    id_formation VARCHAR(20) NOT NULL,
    FOREIGN KEY (id_formation) REFERENCES formation (id)
);

CREATE INDEX ON triplet_affectation (id_formation);