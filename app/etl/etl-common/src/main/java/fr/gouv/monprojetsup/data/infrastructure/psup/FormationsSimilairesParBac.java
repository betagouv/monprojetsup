package fr.gouv.monprojetsup.data.infrastructure.psup;

import fr.gouv.monprojetsup.data.infrastructure.model.stats.PsupStatistiques;

import java.util.HashMap;
import java.util.Map;

record FormationsSimilairesParBac(
        //indexé par I_TC_COD puis par GFlCod
        Map<Integer, Map<String, Integer>> parBac
) {
    public FormationsSimilairesParBac() {
        this(new HashMap<>());
    }

    public void normalize() {
        parBac.values().forEach(m -> {
            if (m.size() >= 2) {
                //bestScore is generally the same filiere, not very meaningful
                int bestScore = m.values().stream().mapToInt(n -> n).max().orElse(0);
                //this represents the best macth
                int secondBestScore = m.values().stream().filter(n -> n != bestScore).mapToInt(n -> n).max().orElse(0);
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
