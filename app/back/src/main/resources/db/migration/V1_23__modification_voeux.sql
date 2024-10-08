WITH updated_data AS (
    SELECT
        id,
        jsonb_agg(
                jsonb_set(
                        elem,
                        '{voeuxChoisis}',
                        elem->'tripletsAffectationsChoisis'
                ) - 'tripletsAffectationsChoisis'
        ) AS new_data
    FROM profil_eleve,
         jsonb_array_elements(formations_favorites) AS elem
    GROUP BY id
)
UPDATE profil_eleve
SET formations_favorites = updated_data.new_data
FROM updated_data
WHERE profil_eleve.id = updated_data.id;
