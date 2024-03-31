package fr.gouv.monprojetsup.data.update.onisep;

import java.util.List;

public record InteretsOnisep(
        List<InteretOnisep> interets
) {
    public record InteretOnisep(
            String id,
            String libelle
    ) {

    }
}
