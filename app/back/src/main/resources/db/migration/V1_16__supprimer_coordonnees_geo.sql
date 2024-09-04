ALTER TABLE ref_triplet_affectation
    DROP COLUMN coordonnees_geographiques,
    ALTER COLUMN latitude SET NOT NULL,
    ALTER COLUMN longitude SET NOT NULL;