package fr.gouv.monprojetsup.data.model.interets;

import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.update.onisep.InteretsOnisep;
import fr.gouv.monprojetsup.data.update.rome.InteretsRome;

import java.util.HashMap;
import java.util.Map;

import static fr.gouv.monprojetsup.data.Constants.CENTRE_INTERETS_ROME;

public record Interets(
        /**
         * indexed by interests fl e.g. "T-IDEO2.4819
         */

        Map<String,String> interets
) {
    public Interets() {
        this(new HashMap<>());
    }

    public Interets(InteretsOnisep interets) {
        this(new HashMap<>());
        interets.interets().forEach(m -> {
            this.interets.put(Constants.cleanup(m.id()), m.libelle());
        });
    }

    public static String getKey(InteretsRome.Item m) {
        return CENTRE_INTERETS_ROME + Math.abs(m.libelle_centre_interet().hashCode());
    }

}
