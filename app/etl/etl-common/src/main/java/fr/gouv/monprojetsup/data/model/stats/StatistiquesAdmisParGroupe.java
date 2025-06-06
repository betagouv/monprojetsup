package fr.gouv.monprojetsup.data.model.stats;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public record StatistiquesAdmisParGroupe(
        //indexé par groupes
        Map<String, StatistiquesAdmisParBac> parGroupe
) implements Serializable {
    public StatistiquesAdmisParGroupe() { this(new HashMap<>()); }

    public void clear() {
        parGroupe.clear();
    }

    public void minimize() {
        parGroupe.keySet().removeIf(k -> !k.equals(PsupStatistiques.TOUS_GROUPES_CODE));
        parGroupe.values().forEach(StatistiquesAdmisParBac::minimize);
        parGroupe.values().removeIf(v -> v.parBac().isEmpty());
    }

    public @NotNull String toString() {
        return String.valueOf(parGroupe.size());
    }

    public void rebuilMiddle50() {
        parGroupe().values().stream()
                .flatMap(e -> e.parBac().values().stream())
                .flatMap(s -> s.parMatiere().entrySet().stream())
                .forEach(e -> e.setValue(e.getValue().updateMiddle50FromFrequencesCumulees()));
    }

    public void removeEmptyGroups() {
        parGroupe.values().forEach(StatistiquesAdmisParBac::removeEmptyGroups);
        parGroupe().values().removeIf(StatistiquesAdmisParBac::isEmpty);
    }

   StatistiquesAdmisParGroupe createGroupAdmisStatistique(
           Map<String, Collection<String>> groups,
           Set<String> bacsKeys
    ) {
       StatistiquesAdmisParGroupe result = new StatistiquesAdmisParGroupe();
        groups.forEach((s, keys) ->
                result.parGroupe.put(
                        s,
                        StatistiquesAdmisParBac.getStatAgregee(
                                keys.stream().distinct().map(parGroupe::get).filter(Objects::nonNull).toList(),
                                bacsKeys
                        )
                )
        );
        return result;
    }

    //par groupe puis par bac
    public @NotNull Map<String,@NotNull Map<@NotNull String, @NotNull Integer>> getAdmisParGroupes() {
        return parGroupe.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().getAdmisParBacs()
        ));
    }

    public TauxSpecialitesParGroupe getStatsSpecialites() {
        return new TauxSpecialitesParGroupe(
                parGroupe.entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> e.getValue().getStatsSpecialites()
                )
                )
        );
    }
}
