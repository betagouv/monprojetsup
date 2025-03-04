UPDATE profil_eleve pe
SET formations_favorites = (
    SELECT jsonb_agg(new_formation)
    FROM (
        -- Keep existing formations
        SELECT formation AS new_formation
        FROM jsonb_array_elements(pe.formations_favorites) AS formation

        UNION ALL

        -- Add new formations for matching idFormation in ref_formation
        SELECT jsonb_build_object(
            'idFormation', rf.las,
            'priseDeNote', NULL,
            'niveauAmbition', 0
        )
        FROM jsonb_array_elements(pe.formations_favorites) AS formation
        JOIN ref_formation rf
            ON formation->>'idFormation' = rf.id
        WHERE rf.las IS NOT NULL
        AND NOT EXISTS (  -- Ensure rf.las is not already in the JSON array
            SELECT 1 FROM jsonb_array_elements(pe.formations_favorites) AS f
            WHERE f->>'idFormation' = rf.las
        )

        UNION ALL

        -- Add the fixed formation 'fl1000000' once per profile if at least one formation matches
        SELECT jsonb_build_object(
            'idFormation', 'fl1000000',
            'priseDeNote', NULL,
            'niveauAmbition', 0
        )
        FROM profil_eleve pe2
        WHERE pe2.id = pe.id
        and NOT EXISTS (
            SELECT 1 FROM jsonb_array_elements(pe.formations_favorites) AS f
            WHERE f->>'idFormation' = 'fl1000000'
        )
    ) AS subquery
)
WHERE EXISTS (
    SELECT 1
    FROM jsonb_array_elements(pe.formations_favorites) AS formation
    JOIN ref_formation rf
        ON formation->>'idFormation' = rf.id
    WHERE rf.las IS NOT NULL
);

UPDATE profil_eleve pe
SET formations_favorites = (
    SELECT jsonb_agg(formation)
    FROM jsonb_array_elements(pe.formations_favorites) AS formation
    LEFT JOIN ref_formation rf
        ON formation->>'idFormation' = rf.id
    WHERE rf.las IS NULL  -- Keep only formations that do NOT have a matching non-null las
)
WHERE EXISTS (
    SELECT 1
    FROM jsonb_array_elements(pe.formations_favorites) AS formation
    JOIN ref_formation rf
        ON formation->>'idFormation' = rf.id
    WHERE rf.las IS NOT NULL
);
