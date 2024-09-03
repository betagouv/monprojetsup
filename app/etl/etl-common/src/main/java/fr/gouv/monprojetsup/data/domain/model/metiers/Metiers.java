package fr.gouv.monprojetsup.data.domain.model.metiers;

import java.util.Map;

public record Metiers(
        /*
         * indexed by metiers fl e.g. MET_7776
         */
        Map<String, MetierIdeoDuSup> metiers
) {


}
