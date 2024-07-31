package fr.gouv.monprojetsup.suggestions.infrastructure.onisep;

import java.util.List;

public record LienFiliereFiliere(
        List<FiliereFilierePaireOnisep> indexation
) {
    public record FiliereFilierePaireOnisep(
            Integer sous_formation,
            Integer formation
    ) {

    }
}
