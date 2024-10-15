-- Ajout colonne temporaire
ALTER TABLE profil_eleve ADD COLUMN nouvel_id VARCHAR(200) UNIQUE;
UPDATE profil_eleve SET nouvel_id = id::text;

ALTER TABLE eleve_compte_parcoursup ADD COLUMN nouvel_id_eleve VARCHAR(200) UNIQUE;
UPDATE eleve_compte_parcoursup SET nouvel_id_eleve = id_eleve::text;

-- Màj clé étrangère dans eleve_compte_parcoursup
ALTER TABLE eleve_compte_parcoursup DROP CONSTRAINT eleve_compte_parcoursup_id_eleve_fkey;
ALTER TABLE eleve_compte_parcoursup ADD CONSTRAINT eleve_compte_parcoursup_id_eleve_fkey
    FOREIGN KEY (nouvel_id_eleve) REFERENCES profil_eleve(nouvel_id);

-- Màj clé primaire dans profil_eleve
ALTER TABLE profil_eleve DROP CONSTRAINT profil_eleve_pkey;
ALTER TABLE profil_eleve ADD CONSTRAINT profil_eleve_pkey PRIMARY KEY (nouvel_id);

-- Màj clé primaire dans eleve_compte_parcoursup
ALTER TABLE eleve_compte_parcoursup DROP CONSTRAINT eleve_compte_parcoursup_pkey;
ALTER TABLE eleve_compte_parcoursup ADD CONSTRAINT eleve_compte_parcoursup_pkey PRIMARY KEY (nouvel_id_eleve);

-- Suppression et renommage
ALTER TABLE profil_eleve DROP COLUMN id;
ALTER TABLE profil_eleve RENAME COLUMN nouvel_id TO id;

ALTER TABLE eleve_compte_parcoursup DROP COLUMN id_eleve;
ALTER TABLE eleve_compte_parcoursup RENAME COLUMN nouvel_id_eleve TO id_eleve;
