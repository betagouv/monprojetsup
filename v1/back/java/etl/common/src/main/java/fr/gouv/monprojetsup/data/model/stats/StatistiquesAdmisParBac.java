package fr.gouv.monprojetsup.data.model.stats;

import java.util.HashMap;
import java.util.Map;

public record StatistiquesAdmisParBac(
        //indexé par I_MT_COD, -1 for all, -2 for my bac
        Map<String, StatistiquesAdmisParMatiere> parBac
) {
    public StatistiquesAdmisParBac() { this(new HashMap<>()); }

    public void set(Map<String, Map<Integer, int[]>> stringMapMap) {
        parBac.clear();
        stringMapMap.forEach((s, integerMapMap)
                -> parBac.computeIfAbsent(s, z-> new StatistiquesAdmisParMatiere())
                .setStatistique(integerMapMap)
        );
    }

    public void minimize() {
        parBac.values().forEach(StatistiquesAdmisParMatiere::minimize);
        StatistiquesAdmisParMatiere s = parBac.get(PsupStatistiques.TOUS_BACS_CODE);
        if(s == null) {
            parBac.clear();
        } else {
            s.parMatiere().keySet().removeIf( m -> m != PsupStatistiques.MOYENNE_GENERALE_CODE && m != PsupStatistiques.MOYENNE_BAC_CODE);
            Statistique ss = s.parMatiere().get(PsupStatistiques.MOYENNE_GENERALE_CODE);
            if (ss == null) {
                parBac.clear();
            } else {
                if (ss.nb() <= 3) {
                    parBac.keySet().removeIf( b -> !b.equals(PsupStatistiques.TOUS_BACS_CODE));
                }
            }
        }
    }

    public void removeSmallPopulations() {
        this.parBac.values().forEach(StatistiquesAdmisParMatiere::removeSmallPopulations);
        this.parBac.values().removeIf(statistiquesAdmisParMatiere -> statistiquesAdmisParMatiere.parMatiere().isEmpty());
    }
}
