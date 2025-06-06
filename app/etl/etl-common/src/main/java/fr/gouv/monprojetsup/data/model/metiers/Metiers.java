package fr.gouv.monprojetsup.data.model.metiers;

import java.util.Map;

public record Metiers(
        /*
         * indexed by metiers fl e.g. MET_7776
         */
        Map<String, MetierIdeo> metiers
) {


}
