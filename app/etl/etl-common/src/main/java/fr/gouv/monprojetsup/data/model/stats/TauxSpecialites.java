package fr.gouv.monprojetsup.data.model.stats;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public record TauxSpecialites(
        Map<String, Integer> parSpecialite
)  implements Serializable {
    public TauxSpecialites() {
        this(new HashMap<>());
    }
}
