package fr.gouv.monprojetsup.data.infrastructure.onisep;

import java.util.ArrayList;
import java.util.List;

public record LienThematiqueMetier(
        List<ThematiqueMetierPaireOnisep> paires
) {
    public LienThematiqueMetier() {
        this(new ArrayList<>());
    }

    public record ThematiqueMetierPaireOnisep(
            String id_formation,//should be id_metier
            String id_thematique
    ) {

    }
}
