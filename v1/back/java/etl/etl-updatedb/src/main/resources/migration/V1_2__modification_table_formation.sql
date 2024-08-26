ALTER TABLE formation
    ADD COLUMN descriptif_conseils  TEXT           NULL,
    ADD COLUMN descriptif_diplome   TEXT           NULL,
    ADD COLUMN points_attendus      VARCHAR(500)[] NULL,
    ADD COLUMN formations_associees VARCHAR(20)[]  NULL;

ALTER TABLE formation
    DROP COLUMN formation_parente,
    DROP COLUMN descriptif_specialites;

CREATE TABLE type_baccalaureat
(
    id  VARCHAR(20) PRIMARY KEY,
    nom VARCHAR(200) NOT NULL
);

CREATE TABLE moyenne_generale_admis
(
    annee               VARCHAR(4) NOT NULL,
    id_formation        VARCHAR    NOT NULL,
    id_bac              VARCHAR    NOT NULL,
    frequences_cumulees INT[40]    NOT NULL,
    PRIMARY KEY (annee, id_formation, id_bac),
    FOREIGN KEY (id_bac) REFERENCES type_baccalaureat,
    FOREIGN KEY (id_formation) REFERENCES formation
);

CREATE TABLE repartition_admis
(
    annee        VARCHAR(4)   NOT NULL,
    id_formation VARCHAR(200) NOT NULL,
    id_bac       VARCHAR      NOT NULL,
    nombre_admis INT          NOT NULL,
    PRIMARY KEY (annee, id_formation, id_bac),
    FOREIGN KEY (id_bac) REFERENCES type_baccalaureat,
    FOREIGN KEY (id_formation) REFERENCES formation
);

CREATE INDEX ON repartition_admis (annee, id_formation);
