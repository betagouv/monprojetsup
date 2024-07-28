package fr.gouv.monprojetsup.data.audit.typesFormation;

import java.util.ArrayList;
import java.util.List;

public record TypeFormationOni(
        String slug,//key
        String libelle,
        String libelle_court,
        String descriptif_type_formation,
        String frCodPsup,
        String frLibPsup
) {
    public TypeFormationOni(TypeFormationOni t, String gFrCod, String gFrLib) {
        this(t.slug,
                t.libelle,
                t.libelle_court,
                t.descriptif_type_formation,
                gFrCod,
                gFrLib
                );
    }

    /*      {
            "slug": "TYPFOR.90",
            "libelle": "Autre classe préparatoire",
            "libelle_court": null,
            "libelle_pluriel": "Les autres classes préparatoires",
            "id_niveau_terminal_etude": null,
            "niveaux_enseignement": null,
            "typologies_generiques": null,
            "descriptif_type_formation": null,
            "descriptif_type_formation_collegien": null
          },
        */
    public record Typesformations(
            Content extraction
    ) {
        public Typesformations() {
            this(new Content());
        }
    }

    public record Content(
            List<TypeFormationOni> content
    ) {
        public Content() {
            this(new ArrayList<>());
        }

    }
}
