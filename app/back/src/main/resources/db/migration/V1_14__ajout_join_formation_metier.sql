
CREATE TABLE ref_join_formation_metier (
    id_formation character varying(20) NOT NULL,
    id_metier character varying(20) NOT NULL
);

ALTER TABLE ONLY ref_join_formation_metier
    ADD CONSTRAINT ref_join_formation_metier_formation_fkey FOREIGN KEY (id_formation) REFERENCES ref_formation(id);

ALTER TABLE ONLY ref_join_formation_metier
    ADD CONSTRAINT ref_join_formation_metier_metier_fkey FOREIGN KEY (id_metier) REFERENCES ref_metier(id);

ALTER TABLE ref_join_formation_metier ADD PRIMARY KEY (id_formation, id_metier);

CREATE INDEX ref_join_formation_metier_formation_id ON ref_join_formation_metier(id_formation);

CREATE INDEX ref_join_formation_metier_metier_id ON ref_join_formation_metier(id_metier);

