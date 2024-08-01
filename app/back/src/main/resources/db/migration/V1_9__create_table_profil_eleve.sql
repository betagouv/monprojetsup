CREATE TABLE profil_eleve
(
    id                   uuid PRIMARY KEY,
    situation            VARCHAR(20) NULL CHECK (situation IN ('AUCUNE_IDEE', 'QUELQUES_PISTES', 'PROJET_PRECIS')),
    classe               VARCHAR(20) NULL CHECK (classe IN ('SECONDE', 'PREMIERE', 'TERMINALE', 'NON_RENSEIGNE')),
    id_baccalaureat      VARCHAR(20) NULL,
    specialites          VARCHAR[]   NULL,
    domaines             VARCHAR[]   NULL,
    centres_interets     VARCHAR[]   NULL,
    metiers_favoris      VARCHAR[]   NULL,
    duree_etudes_prevue  VARCHAR     NULL CHECK (duree_etudes_prevue IN
                                                 ('INDIFFERENT', 'COURTE', 'LONGUE', 'AUCUNE_IDEE', 'NON_RENSEIGNE')),
    alternance           VARCHAR     NULL CHECK (alternance IN
                                                 ('PAS_INTERESSE', 'INDIFFERENT', 'INTERESSE', 'TRES_INTERESSE',
                                                  'NON_RENSEIGNE')),
    communes_favorites   jsonb       NULL,
    moyenne_generale     float       NULL,
    formations_favorites VARCHAR[]   NULL,

    FOREIGN KEY (id_baccalaureat) REFERENCES baccalaureat (id)

);
