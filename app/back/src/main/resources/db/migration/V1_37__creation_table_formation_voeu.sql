CREATE TABLE ref_join_formation_voeu(
    id_formation varchar(255) NOT NULL,
    id_voeu varchar(255) NOT NULL,
    PRIMARY KEY (id_formation, id_voeu),
    CONSTRAINT fk_ref_join_formation_voeu_id_formation FOREIGN key(id_formation) REFERENCES ref_formation(id),
    CONSTRAINT fk_ref_join_formation_voeu_id_voeu FOREIGN key(id_voeu) REFERENCES ref_voeu(id)
);
CREATE INDEX idx_ref_join_formation_voeu_id_formation ON ref_join_formation_voeu(id_formation);
CREATE INDEX idx_ref_join_formation_voeu_id_voeu ON ref_join_formation_voeu(id_voeu);
ALTER TABLE ref_voeu DROP COLUMN id_formation;