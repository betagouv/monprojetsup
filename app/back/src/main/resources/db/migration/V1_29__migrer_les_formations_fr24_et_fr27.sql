UPDATE profil_eleve
SET formations_favorites = profil_eleve.formations_favorites || jsonb_build_array(
        jsonb_build_object(
                'idFormation', 'fl230',
                'priseDeNote', (SELECT value ->> 'priseDeNote' FROM jsonb_array_elements(formations_favorites) AS value WHERE value ->> 'idFormation' = 'fr24' LIMIT 1),
                'voeuxChoisis', '[]'::jsonb,
                'niveauAmbition', (SELECT value ->> 'niveauAmbition' FROM jsonb_array_elements(formations_favorites) AS value WHERE value ->> 'idFormation' = 'fr24' LIMIT 1)
        ),
        jsonb_build_object(
                'idFormation', 'fl240',
                'priseDeNote', (SELECT value ->> 'priseDeNote' FROM jsonb_array_elements(formations_favorites) AS value WHERE value ->> 'idFormation' = 'fr24' LIMIT 1),
                'voeuxChoisis', '[]'::jsonb,
                'niveauAmbition', (SELECT value ->> 'niveauAmbition' FROM jsonb_array_elements(formations_favorites) AS value WHERE value ->> 'idFormation' = 'fr24' LIMIT 1)
        )
                                                                )
WHERE formations_favorites @> '[{"idFormation": "fr24"}]';

UPDATE profil_eleve
SET formations_favorites = (
    SELECT jsonb_agg(formation)
    FROM (
             SELECT formation
             FROM jsonb_array_elements(formations_favorites) AS formation
             WHERE formation ->> 'idFormation' <> 'fr24'
         ) AS formations_filtrees
)
WHERE formations_favorites @> '[{"idFormation": "fr24"}]';

UPDATE profil_eleve
SET corbeille_formations = (
    SELECT array_cat(
                   array_remove(corbeille_formations, 'fr24'),
                   ARRAY['fl230', 'fl240']
           )
)
WHERE 'fr24' = ANY(corbeille_formations);

UPDATE profil_eleve
SET formations_favorites = profil_eleve.formations_favorites || jsonb_build_array(
        jsonb_build_object(
                'idFormation', 'fl270',
                'priseDeNote', (SELECT value ->> 'priseDeNote' FROM jsonb_array_elements(formations_favorites) AS value WHERE value ->> 'idFormation' = 'fr27' LIMIT 1),
                'voeuxChoisis', '[]'::jsonb,
                'niveauAmbition', (SELECT value ->> 'niveauAmbition' FROM jsonb_array_elements(formations_favorites) AS value WHERE value ->> 'idFormation' = 'fr27' LIMIT 1)
        ),
        jsonb_build_object(
                'idFormation', 'fl271',
                'priseDeNote', (SELECT value ->> 'priseDeNote' FROM jsonb_array_elements(formations_favorites) AS value WHERE value ->> 'idFormation' = 'fr27' LIMIT 1),
                'voeuxChoisis', '[]'::jsonb,
                'niveauAmbition', (SELECT value ->> 'niveauAmbition' FROM jsonb_array_elements(formations_favorites) AS value WHERE value ->> 'idFormation' = 'fr27' LIMIT 1)
        )
                                                                )
WHERE formations_favorites @> '[{"idFormation": "fr27"}]';

UPDATE profil_eleve
SET formations_favorites = (
    SELECT jsonb_agg(formation)
    FROM (
             SELECT formation
             FROM jsonb_array_elements(formations_favorites) AS formation
             WHERE formation ->> 'idFormation' <> 'fr27'
         ) AS formations_filtrees
)
WHERE formations_favorites @> '[{"idFormation": "fr27"}]';

UPDATE profil_eleve
SET corbeille_formations = (
    SELECT array_cat(
                   array_remove(corbeille_formations, 'fr27'),
                   ARRAY['fl270', 'fl271']
           )
)
WHERE 'fr27' = ANY(corbeille_formations);

