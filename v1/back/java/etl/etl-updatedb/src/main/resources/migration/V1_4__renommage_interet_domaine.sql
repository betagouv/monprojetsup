ALTER TABLE groupement_domaine
    RENAME TO domaine_categorie;
ALTER TABLE domaine
    RENAME COLUMN id_groupement TO id_categorie;

ALTER TABLE groupement_interet
    RENAME TO interet_categorie;
ALTER TABLE interet
    RENAME COLUMN id_groupement TO id_categorie;
ALTER TABLE interet
    RENAME TO interet_sous_categorie;

CREATE TABLE interet
(
    id                VARCHAR(30)  NOT NULL PRIMARY KEY,
    nom               VARCHAR(200) NOT NULL,
    id_sous_categorie VARCHAR(30)  NOT NULL REFERENCES interet_sous_categorie
);