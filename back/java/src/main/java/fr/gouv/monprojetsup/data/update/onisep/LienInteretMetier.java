package fr.gouv.monprojetsup.data.update.onisep;

import java.util.List;

public record LienInteretMetier(
        List<InteretMetierPaireOnisep> paires
) {
    public record InteretMetierPaireOnisep(
            String id_formation,//should be id_metier
            String id_centre_interet
    ) {

    }
}
