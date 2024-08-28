package fr.gouv.monprojetsup.data.domain.model.stats;

import java.util.HashMap;
import java.util.Map;

record TauxSpecialites(
        Map<Integer, Integer> parSpecialite
) {
    public TauxSpecialites() {
        this(new HashMap<>());
    }
}
