package fr.gouv.monprojetsup.data.domain.model.stats;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

record TauxSpecialites(
        Map<Integer, Integer> parSpecialite
)  implements Serializable {
    public TauxSpecialites() {
        this(new HashMap<>());
    }
}
