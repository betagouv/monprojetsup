package fr.gouv.monprojetsup.data.domain.model.stats;

import java.util.HashMap;
import java.util.Map;

record TauxSpecialitesParGroupe(
        Map<String, TauxSpecialites> parGroupe
) {
    public TauxSpecialitesParGroupe() {
        this(new HashMap<>());
    }
}
