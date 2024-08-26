CREATE TABLE join_baccalaureat_specialite
(
    id_baccalaureat VARCHAR(20) NOT NULL,
    id_specialite   VARCHAR(20) NOT NULL,
    FOREIGN KEY (id_baccalaureat) REFERENCES baccalaureat (id),
    FOREIGN KEY (id_specialite) REFERENCES specialite (id),
    PRIMARY KEY (id_baccalaureat, id_specialite)
);
