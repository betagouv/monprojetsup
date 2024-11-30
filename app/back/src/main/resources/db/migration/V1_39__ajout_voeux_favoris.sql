ALTER TABLE profil_eleve ADD COLUMN voeux_favoris jsonb NULL;

UPDATE profil_eleve
SET voeux_favoris = to_jsonb((
    SELECT ARRAY(
         Select jsonb_build_object('idVoeu', voeux, 'estFavoriParcoursup', false)
         FROM (
             SELECT DISTINCT voeux
               FROM (
                    SELECT jsonb_array_elements_text(formation->'voeux_choisis') AS voeux
                    FROM jsonb_array_elements(profil_eleve.formations_favorites) formation
               ) AS subquery
          ) AS voeux_uniques
    )
))
WHERE profil_eleve.formations_favorites IS NOT NULL;


UPDATE profil_eleve
SET formations_favorites = to_jsonb(
    ARRAY(
        SELECT jsonb_strip_nulls(jsonb_set(formation, '{voeuxChoisis}', 'null', true))
        FROM jsonb_array_elements(profil_eleve.formations_favorites) AS formation
    )
)
WHERE profil_eleve.formations_favorites IS NOT NULL;