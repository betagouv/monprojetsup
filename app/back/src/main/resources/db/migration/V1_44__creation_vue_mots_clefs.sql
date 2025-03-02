CREATE OR REPLACE VIEW expanded_keywords AS
SELECT distinct id,
                label,
                                  unaccent(lower(trim(word))) AS mot_clef
                           FROM ref_formation r,
                           unnest(mots_clefs) AS mot_clef,
                           unnest(string_to_array(mot_clef, ' ')) AS word
                           WHERE r.obsolete = false AND LENGTH(trim(word)) >= 2;

CREATE OR REPLACE VIEW expanded_label AS
SELECT distinct
        id,
        label,
        unaccent(lower(trim(label))) AS label_sans_accents,
        unaccent(lower(trim(label_decoupe_brut))) AS label_decoupe
FROM ref_formation,
       unnest(string_to_array(regexp_replace(label, '[^0-9A-Za-zÀ-ÖØ-öø-ÿ]+', ' ', 'g'), ' ')) AS label_decoupe_brut
WHERE LENGTH(label_decoupe_brut) >= 2 and obsolete = false;
