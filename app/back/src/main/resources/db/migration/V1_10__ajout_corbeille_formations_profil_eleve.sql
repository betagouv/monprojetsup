ALTER TABLE profil_eleve
    ADD COLUMN corbeille_formations VARCHAR[] NOT NULL DEFAULT ARRAY[]::VARCHAR[];
