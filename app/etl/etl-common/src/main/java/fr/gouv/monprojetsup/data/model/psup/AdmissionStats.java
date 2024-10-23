package fr.gouv.monprojetsup.data.model.psup;

import fr.gouv.monprojetsup.data.model.stats.Statistique;
import fr.gouv.monprojetsup.data.model.stats.StatistiquesAdmisParBac;
import fr.gouv.monprojetsup.data.model.stats.TauxSpecialites;
import fr.gouv.monprojetsup.data.model.stats.TauxSpecialitesParGroupe;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.model.stats.PsupStatistiques.MATIERE_MOYENNE_GENERALE_CODE;
import static fr.gouv.monprojetsup.data.model.stats.PsupStatistiques.MIN_POPULATION_SIZE_FOR_STATS;

public record AdmissionStats(
        int annee,
        Map<String, StatistiquesAdmisParBac> parGroupe,
        @NotNull Map<String, @NotNull Map<@NotNull String, @NotNull Integer>> admisParGroupes,
        @NotNull TauxSpecialitesParGroupe statsSpecialites
) {

    public @NotNull Map<String, @NotNull Integer> getNbAdmisParBac(String g) {
        return admisParGroupes.getOrDefault(g, Map.of());
    }

    @NotNull
    public Map<String, Integer> getPctAdmisParSpec(@NotNull String mpsKey) {
        return toPct(getNbAdmisParSpec(mpsKey));
    }

    private static <T> @NotNull Map<T, @NotNull Integer> toPct(Map<T, @NotNull Integer> nbAdmis) {
        if(nbAdmis == null || nbAdmis.isEmpty()) return Map.of();
        int total = nbAdmis.values().stream().mapToInt(Integer::intValue).sum();
        if (total <= MIN_POPULATION_SIZE_FOR_STATS) return Map.of();
        return nbAdmis.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> (int) Math.round(100.0 * e.getValue() / total)
                ));
    }

    @NotNull
    public Map<String,  @NotNull Statistique> getStatsMoyGenParBac(String grp) {
        return getStatScolParBac(grp, MATIERE_MOYENNE_GENERALE_CODE);
    }

    public Map<String, @NotNull Statistique> getStatScolParBac(String groupe, String matiere) {
        @Nullable StatistiquesAdmisParBac toto = parGroupe.get(groupe);
        if(toto == null) return Map.of();
        return toto.parBac().entrySet().stream()
                .map(e -> Pair.of(e.getKey(), e.getValue().parMatiere().get(matiere)))
                .filter(p -> p.getRight() != null)
                .collect(
                        Collectors.toMap(
                                Pair::getLeft,
                                Pair::getRight
                        )
                );
    }


    public @NotNull Map<String, Integer> getNbAdmisParSpec(String g) {
        TauxSpecialites s = statsSpecialites.parGroupe().get(g);
        if(s == null) return Map.of();
        return s.parSpecialite();
    }



}
