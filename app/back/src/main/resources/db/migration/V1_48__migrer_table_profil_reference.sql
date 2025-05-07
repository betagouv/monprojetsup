
ALTER TABLE profil_reference ADD CONSTRAINT profil_reference_alternance_check CHECK (((alternance)::text = ANY ((ARRAY['PAS_INTERESSE'::character varying, 'INDIFFERENT'::character varying, 'INTERESSE'::character varying, 'TRES_INTERESSE'::character varying, 'NON_RENSEIGNE'::character varying])::text[])));
ALTER TABLE profil_reference ADD CONSTRAINT profil_reference_classe_check CHECK (((classe)::text = ANY ((ARRAY['SECONDE'::character varying, 'PREMIERE'::character varying, 'TERMINALE'::character varying, 'NON_RENSEIGNE'::character varying])::text[])));
ALTER TABLE profil_reference ADD CONSTRAINT profil_reference_duree_etudes_prevue_check CHECK (((duree_etudes_prevue)::text = ANY ((ARRAY['INDIFFERENT'::character varying, 'COURTE'::character varying, 'LONGUE'::character varying, 'AUCUNE_IDEE'::character varying, 'NON_RENSEIGNE'::character varying])::text[])));
ALTER TABLE profil_reference ADD CONSTRAINT profil_reference_situation_check CHECK (((situation)::text = ANY ((ARRAY['AUCUNE_IDEE'::character varying, 'QUELQUES_PISTES'::character varying, 'PROJET_PRECIS'::character varying])::text[])));
ALTER TABLE ONLY public.profil_reference
    ADD CONSTRAINT profil_reference_id_ref_baccalaureat_fkey FOREIGN KEY (id_baccalaureat) REFERENCES ref_baccalaureat(id);
