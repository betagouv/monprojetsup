ALTER TABLE profil_eleve
    DROP COLUMN formations_favorites;
ALTER TABLE profil_eleve
    ADD COLUMN formations_favorites jsonb NULL ;
