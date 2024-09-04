package fr.gouv.monprojetsup.data.domain.model.onisep;

import fr.gouv.monprojetsup.data.domain.Constants;

import java.util.List;
import java.util.Set;

public record InteretsOnisep(
        List<InteretOnisep> interets
) {
    public void retainAll(Set<String> interetsUsed) {
        interets.removeIf(i -> !interetsUsed.contains(Constants.cleanup(i.id())));
    }

    public record InteretOnisep(
            String id,
            String libelle
    ) {

    }
}
