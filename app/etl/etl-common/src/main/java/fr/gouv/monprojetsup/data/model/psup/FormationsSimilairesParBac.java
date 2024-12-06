package fr.gouv.monprojetsup.data.model.psup;

import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;

import java.util.HashMap;
import java.util.Map;

record FormationsSimilairesParBac(
        //indexé par I_TC_COD puis par GFlCod
        Map<Integer, Map<String, Long>> parBac
) {
    public FormationsSimilairesParBac() {
        this(new HashMap<>());
    }

    public void normalize() {
        parBac.values().forEach(m -> {
            if (m.size() >= 2) {
                //bestScore is generally the same filiere, not very meaningful
                long bestScore = m.values().stream().mapToLong(n -> n).max().orElse(0);
                //this represents the best macth
                long secondBestScore = m.values().stream().filter(n -> n != bestScore).mapToLong(n -> n).max().orElse(0);
                if (secondBestScore > 0) {
                    m.entrySet().forEach(e -> e.setValue(PsupStatistiques.SIM_FIL_MAX_WEIGHT * e.getValue() / secondBestScore));
                }
            } else {
                //we drop this useless data
                m.clear();
            }
        });
        //some cleanup, may improve performances?
        parBac.entrySet().removeIf(e -> e.getValue().isEmpty());
    }
}
