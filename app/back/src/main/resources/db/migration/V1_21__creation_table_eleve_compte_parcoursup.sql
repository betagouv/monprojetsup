CREATE TABLE eleve_compte_parcoursup
(
    id_eleve         UUID PRIMARY KEY,
    id_parcoursup    INT NOT NULL,
    date_mise_a_jour DATE NOT NULL,
    FOREIGN KEY (id_eleve) REFERENCES profil_eleve (id)
);