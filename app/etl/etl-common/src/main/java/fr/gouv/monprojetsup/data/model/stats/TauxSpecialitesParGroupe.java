package fr.gouv.monprojetsup.data.model.stats;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

record TauxSpecialitesParGroupe(
        Map<String, TauxSpecialites> parGroupe
) implements Serializable {
    public TauxSpecialitesParGroupe() {
        this(new HashMap<>());
    }
}
