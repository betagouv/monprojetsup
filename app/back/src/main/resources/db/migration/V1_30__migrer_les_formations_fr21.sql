UPDATE profil_eleve
SET formations_favorites = profil_eleve.formations_favorites || jsonb_build_array(
        jsonb_build_object(
                'idFormation', 'fl241',
                'priseDeNote', (SELECT value ->> 'priseDeNote' FROM jsonb_array_elements(formations_favorites) AS value WHERE value ->> 'idFormation' = 'fl230' LIMIT 1),
                'voeuxChoisis', '[]'::jsonb,
                'niveauAmbition', (SELECT value ->> 'niveauAmbition' FROM jsonb_array_elements(formations_favorites) AS value WHERE value ->> 'idFormation' = 'fl230' LIMIT 1)
        ),
        jsonb_build_object(
                'idFormation', 'fl242',
                'priseDeNote', (SELECT value ->> 'priseDeNote' FROM jsonb_array_elements(formations_favorites) AS value WHERE value ->> 'idFormation' = 'fl230' LIMIT 1),
                'voeuxChoisis', '[]'::jsonb,
                'niveauAmbition', (SELECT value ->> 'niveauAmbition' FROM jsonb_array_elements(formations_favorites) AS value WHERE value ->> 'idFormation' = 'fl230' LIMIT 1)
        )
                                                                )
WHERE formations_favorites @> '[{"idFormation": "fl230"}]';

UPDATE profil_eleve
SET formations_favorites = (
    SELECT jsonb_agg(formation)
    FROM (
             SELECT formation
             FROM jsonb_array_elements(formations_favorites) AS formation
             WHERE formation ->> 'idFormation' <> 'fl230'
         ) AS formations_filtrees
)
WHERE formations_favorites @> '[{"idFormation": "fl230"}]';

UPDATE profil_eleve
SET corbeille_formations = (
    SELECT array_cat(
                   array_remove(corbeille_formations, 'fl230'),
                   ARRAY['fl241', 'fl242']
           )
)
WHERE 'fl230' = ANY(corbeille_formations);




UPDATE profil_eleve
SET formations_favorites = profil_eleve.formations_favorites || jsonb_build_array(
        jsonb_build_object(
                'idFormation', 'fl230',
                'priseDeNote', (SELECT value ->> 'priseDeNote' FROM jsonb_array_elements(formations_favorites) AS value WHERE value ->> 'idFormation' = 'fr21' LIMIT 1),
                'voeuxChoisis', '[]'::jsonb,
                'niveauAmbition', (SELECT value ->> 'niveauAmbition' FROM jsonb_array_elements(formations_favorites) AS value WHERE value ->> 'idFormation' = 'fr21' LIMIT 1)
        ),
        jsonb_build_object(
                'idFormation', 'fl210',
                'priseDeNote', (SELECT value ->> 'priseDeNote' FROM jsonb_array_elements(formations_favorites) AS value WHERE value ->> 'idFormation' = 'fr21' LIMIT 1),
                'voeuxChoisis', '[]'::jsonb,
                'niveauAmbition', (SELECT value ->> 'niveauAmbition' FROM jsonb_array_elements(formations_favorites) AS value WHERE value ->> 'idFormation' = 'fr21' LIMIT 1)
        )
                                                                )
WHERE formations_favorites @> '[{"idFormation": "fr21"}]';

UPDATE profil_eleve
SET formations_favorites = (
    SELECT jsonb_agg(formation)
    FROM (
             SELECT formation
             FROM jsonb_array_elements(formations_favorites) AS formation
             WHERE formation ->> 'idFormation' <> 'fr21'
         ) AS formations_filtrees
)
WHERE formations_favorites @> '[{"idFormation": "fr21"}]';

UPDATE profil_eleve
SET corbeille_formations = (
    SELECT array_cat(
                   array_remove(corbeille_formations, 'fr21'),
                   ARRAY['fl230', 'fl210']
           )
)
WHERE 'fr21' = ANY(corbeille_formations);

