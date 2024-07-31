package fr.gouv.monprojetsup.suggestions.infrastructure.onisep;

import java.util.List;

public record LienFiliereFormation(
        List<FiliereFormationPaireOnisep> indexation
) {
    public record FiliereFormationPaireOnisep(
            Integer g_ta_cod,
            Integer g_fl_cod
    ) {

    }
}
