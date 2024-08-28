package fr.gouv.monprojetsup.data.domain.model.stats;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.domain.model.stats.PsupStatistiques.MIN_POPULATION_SIZE_FOR_STATS;
import static fr.gouv.monprojetsup.data.domain.model.stats.PsupStatistiques.MOYENNE_BAC_CODE;

record StatistiquesAdmisParMatiere(
        //indexé par I_TC_COD
        Map<Integer, Statistique> parMatiere
) {
    public StatistiquesAdmisParMatiere() {
        this(new HashMap<>());
    }

    public static StatistiquesAdmisParMatiere getStatAgregee(List<StatistiquesAdmisParMatiere> toList) {

        return new StatistiquesAdmisParMatiere(
                toList.stream()
                        .flatMap(e -> e.parMatiere.entrySet().stream())
                        .collect(Collectors.groupingBy(Map.Entry::getKey))
                        .entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> Statistique.getStatAgregee(e.getValue().stream().map(Map.Entry::getValue).toList())
                        ))
        );
    }

    public void setStatistique(Map<Integer, int[]> integerMapMap) {
        parMatiere.clear();
        integerMapMap.forEach(
                (matiere, integerIntegerMap)
                        -> parMatiere.put(matiere, Statistique.getStatistiqueFromCompteurs(integerIntegerMap))
        );
    }

    private static final List<Integer> matieresPrincipales
            = Arrays.asList(PsupStatistiques.MOYENNE_GENERALE_CODE, MOYENNE_BAC_CODE, 1, 2, 3, 4, 5, 6);

    public void minimize() {
        parMatiere.keySet().retainAll(matieresPrincipales);
    }

    public void removeSmallPopulations() {
        this.parMatiere.values().removeIf(statistique -> statistique.nb() < MIN_POPULATION_SIZE_FOR_STATS);
    }
}
