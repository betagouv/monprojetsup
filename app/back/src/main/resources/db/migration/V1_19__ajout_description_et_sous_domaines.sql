ALTER TABLE ref_domaine ADD COLUMN description text NULL;

ALTER TABLE ref_interet_categorie DROP COLUMN id_categorie;

CREATE TABLE ref_domaine_ideo (
    id character varying(255) NOT NULL,
    nom character varying(255) NOT NULL,
    id_domaine_mps character varying(255) NOT NULL
);
