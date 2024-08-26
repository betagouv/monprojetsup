ALTER TABLE type_baccalaureat
    ADD COLUMN id_externe VARCHAR(20);

UPDATE type_baccalaureat
SET id_externe = 'indéfini'
WHERE id_externe IS NULL;

ALTER TABLE type_baccalaureat
    ALTER COLUMN id_externe SET NOT NULL;

ALTER TABLE type_baccalaureat
    RENAME TO baccalaureat;


CREATE TABLE groupement_interet
(
    id    VARCHAR(30) PRIMARY KEY,
    nom   VARCHAR(200) NOT NULL,
    emoji VARCHAR(10)  NOT NULL
);

CREATE TABLE interet
(
    id            VARCHAR(30) PRIMARY KEY,
    nom           VARCHAR(200) NOT NULL,
    id_groupement VARCHAR(30)  NOT NULL,
    emoji         VARCHAR(10)  NOT NULL,
    FOREIGN KEY (id_groupement) REFERENCES groupement_interet
);

CREATE TABLE groupement_domaine
(
    id    VARCHAR(30) PRIMARY KEY,
    nom   VARCHAR(200) NOT NULL,
    emoji VARCHAR(10)  NOT NULL
);

CREATE TABLE domaine
(
    id            VARCHAR(30) PRIMARY KEY,
    nom           VARCHAR(200) NOT NULL,
    id_groupement VARCHAR(30)  NOT NULL,
    emoji         VARCHAR(10)  NOT NULL,
    FOREIGN KEY (id_groupement) REFERENCES groupement_domaine
);

CREATE TABLE critere_analyse_candidature
(
    id    VARCHAR(20) PRIMARY KEY,
    index int UNIQUE,
    nom   VARCHAR(200) NOT NULL
);

ALTER TABLE formation
    DROP COLUMN points_attendus;

ALTER TABLE formation
    ADD COLUMN criteres_analyse int[];
UPDATE formation
SET criteres_analyse = '{}'
WHERE criteres_analyse IS NULL;
ALTER TABLE formation
    ALTER COLUMN criteres_analyse SET NOT NULL;

ALTER TABLE formation
    DROP COLUMN urls;

ALTER TABLE formation
    ADD COLUMN liens jsonb[];
UPDATE formation
SET liens = '{}'
WHERE liens IS NULL;
ALTER TABLE formation
    ALTER COLUMN liens SET NOT NULL;

ALTER TABLE metier
    DROP COLUMN urls;

ALTER TABLE metier
    ADD COLUMN liens jsonb[];
UPDATE metier
SET liens = '{}'
WHERE liens IS NULL;
ALTER TABLE metier
    ALTER COLUMN liens SET NOT NULL;