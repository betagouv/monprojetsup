package fr.gouv.monprojetsup.suggestions.infrastructure.onisep;

import java.util.List;

public record LienFormationMetier(
        List<FormationMetierPaireOnisep> paires
) {

    public record FormationMetierPaireOnisep(
            Integer gFlCod,
            String ID_METIER
    ) {

    }
}
