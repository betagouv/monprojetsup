package fr.gouv.monprojetsup.data.model.stats;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.model.stats.PsupStatistiques.MATIERE_ADMIS_CODE;
import static fr.gouv.monprojetsup.data.model.stats.PsupStatistiques.MATIERE_MOYENNE_BAC_CODE;
import static fr.gouv.monprojetsup.data.model.stats.PsupStatistiques.MATIERE_MOYENNE_GENERALE_CODE;

public record StatistiquesAdmisParMatiere(
        //indexé par I_TC_COD
        Map<String, Statistique> parMatiere
) implements Serializable  {
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

    public void setStatistique(Map<String, int[]> integerMapMap) {
        parMatiere.clear();
        integerMapMap.forEach(
                (matiere, integerIntegerMap)
                        -> parMatiere.put(matiere, Statistique.getStatistiqueFromCompteurs(integerIntegerMap))
        );
    }

    private static final List<String> matieresPrincipales
            = Arrays.asList(
                    PsupStatistiques.MATIERE_MOYENNE_GENERALE_CODE,
                    MATIERE_MOYENNE_BAC_CODE);

    public void minimize() {
        parMatiere.keySet().retainAll(matieresPrincipales);
    }

    public void removeEmptyGroups() {
        this.parMatiere.values().removeIf(statistique -> statistique.nb() <= 0);
    }

    public boolean isEmpty() {
        return this.parMatiere.isEmpty();
    }

    public int getNbAvecMoyGen() {
        Statistique stat = parMatiere.get(MATIERE_ADMIS_CODE);
        if(stat == null) stat = parMatiere.get(MATIERE_MOYENNE_GENERALE_CODE);
        if(stat == null) return 0;
        return stat.nb();
    }

    public TauxSpecialites getStatsSpecialites() {
        return new TauxSpecialites(
                parMatiere.entrySet().stream()
                        .filter(e ->
                                !e.getKey().equals(MATIERE_ADMIS_CODE)
                                        && !e.getKey().equals(MATIERE_MOYENNE_GENERALE_CODE)
                                        && !e.getKey().equals(MATIERE_MOYENNE_BAC_CODE))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().nb()
                        ))
        );
    }
}
