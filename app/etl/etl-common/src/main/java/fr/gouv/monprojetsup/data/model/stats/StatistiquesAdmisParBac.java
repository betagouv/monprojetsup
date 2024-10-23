package fr.gouv.monprojetsup.data.model.stats;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record StatistiquesAdmisParBac(
        //indexé par I_MT_COD, -1 for all, -2 for my bac
        Map<String, StatistiquesAdmisParMatiere> parBac
) implements Serializable  {
    public StatistiquesAdmisParBac() { this(new HashMap<>()); }

    public void set(Map<String, Map<String, int[]>> stringMapMap) {
        parBac.clear();
        stringMapMap.forEach((s, integerMapMap)
                -> parBac.computeIfAbsent(s, z-> new StatistiquesAdmisParMatiere())
                .setStatistique(integerMapMap)
        );
    }

    public void minimize() {
        parBac.values().forEach(StatistiquesAdmisParMatiere::minimize);
        StatistiquesAdmisParMatiere s1 = parBac.get(PsupStatistiques.TOUS_BACS_CODE_LEGACY);
        StatistiquesAdmisParMatiere s2 = parBac.get(PsupStatistiques.TOUS_BACS_CODE_MPS);
        StatistiquesAdmisParMatiere s = s2 != null ? s2 : s1;
        if(s == null) {
            parBac.clear();
        } else {
            s.parMatiere().keySet().removeIf( m -> !m.equals(PsupStatistiques.MATIERE_MOYENNE_GENERALE_CODE) && !m.equals(PsupStatistiques.MATIERE_MOYENNE_BAC_CODE));
            Statistique ss = s.parMatiere().get(PsupStatistiques.MATIERE_MOYENNE_GENERALE_CODE);
            if (ss == null) {
                parBac.clear();
            } else {
                if (ss.nb() <= 3) {
                    parBac.keySet().removeIf( b -> !b.equals(PsupStatistiques.TOUS_BACS_CODE_LEGACY) && !b.equals(PsupStatistiques.TOUS_BACS_CODE_MPS));
                }
            }
        }
    }

    public static StatistiquesAdmisParBac getStatAgregee(
            @NotNull List<@NotNull StatistiquesAdmisParBac> toList,
            @NotNull Set<@NotNull String> bacsKeys
    ) {

        return new StatistiquesAdmisParBac(
                toList.stream()
                        .flatMap(e -> e.parBac.entrySet().stream())
                        .filter(e -> bacsKeys.contains(e.getKey()))
                        .collect(Collectors.groupingBy(Map.Entry::getKey))
                        .entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> StatistiquesAdmisParMatiere.getStatAgregee(e.getValue().stream().map(Map.Entry::getValue).toList())
                        ))
        );
    }


    public void removeEmptyGroups() {
        parBac.values().forEach(StatistiquesAdmisParMatiere::removeEmptyGroups);
        parBac.values().removeIf(StatistiquesAdmisParMatiere::isEmpty);
    }

    public boolean isEmpty() {
        return parBac.isEmpty();
    }

    public @NotNull Map<@NotNull String, @NotNull Integer> getAdmisParBacs() {
        return parBac.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().getNbAvecMoyGen()
        ));
    }

    public TauxSpecialites getStatsSpecialites() {
        return parBac.getOrDefault(
                PsupStatistiques.TOUS_BACS_CODE_MPS,
                new StatistiquesAdmisParMatiere()
        ).getStatsSpecialites();
    }
}
