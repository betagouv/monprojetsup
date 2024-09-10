package fr.gouv.monprojetsup.data.domain.model.stats;

import fr.gouv.monprojetsup.data.tools.Serialisation;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;


public class PsupStatistiques implements Serializable {

    public static final int MIN_POPULATION_SIZE_FOR_STATS = 10;
    public static final int SIM_FIL_MAX_WEIGHT = 100000;

    public static final String TOUS_GROUPES_CODE = "";

    public static final String TOUS_BACS_CODE_LEGACY = "";
    public static final String TOUS_BACS_CODE_MPS = "NC";
    public static final String TOUS_BACS_CODE_FRONT_LIBELLE = "Non-communiqué";

    public static final int PRECISION_PERCENTILES = 40;
    public static final int MATIERE_MOYENNE_GENERALE_CODE = -1;
    public static final int MATIERE_MOYENNE_BAC_CODE = -2;
    public static final int MATIERE_ADMIS_CODE = -3;


    /* année de référence */
    private @Nullable Integer annee;

    /* fréquences cumulées et middle 50 par voeu (ta***) par bac par matière */
    private final StatistiquesAdmisParGroupe statsAdmis = new StatistiquesAdmisParGroupe();

    //par groupe puis par bac
    public final @NotNull Map<@NotNull Integer, @NotNull String> matieres = new TreeMap<>();

    private AdmisMatiereBacAnneeStats admisMatiereBacAnneeStats;

    //par groupe puis par bac
    private transient @NotNull Map<String,@NotNull Map<@NotNull String, @NotNull Integer>> admisParGroupes = Map.of();

    /* stats sur les spécialités des admis, vide pour les las */
    private transient TauxSpecialitesParGroupe statsSpecialites;

    public PsupStatistiques() {

    }

    public static PsupStatistiques build(
            PsupStatistiques stats,
            Set<String> bacsKeys,
            Map<String,String> groups
    ) {
        val statsAdmisParGroupe
                = stats.statsAdmis.createGroupAdmisStatistique(groups, bacsKeys);

        statsAdmisParGroupe.rebuilMiddle50();
        statsAdmisParGroupe.removeEmptyGroups();

        val result = new PsupStatistiques();
        result.setAnnee(stats.getAnnee());
        result.statsAdmis.parGroupe().putAll(statsAdmisParGroupe.parGroupe());
        result.matieres.putAll(stats.matieres);
        result.admisParGroupes = statsAdmisParGroupe.getAdmisParGroupes();
        result.statsSpecialites = statsAdmisParGroupe.getStatsSpecialites();
        result.admisMatiereBacAnneeStats = stats.admisMatiereBacAnneeStats;

        return result;
    }


    public @NotNull Map<String, @NotNull Integer> getNbAdmisParBac(String g) {
        return admisParGroupes.getOrDefault(g, Map.of());
    }


    public @Nullable Map<Integer, Integer> getNbAdmisParSpec(String g) {
        TauxSpecialites s = statsSpecialites.parGroupe().get(g);
        if(s == null) return null;
        return s.parSpecialite();
    }

    @NotNull
    public Map<@NotNull String, @NotNull Integer> getPctAdmisParBac(@NotNull String mpsKey) {
        return toPct(getNbAdmisParBac(mpsKey));
    }

    @NotNull
    public Map<Integer, Integer> getPctAdmisParSpec(@NotNull String mpsKey) {
        return toPct(getNbAdmisParSpec(mpsKey));
    }


    private static <T> @NotNull Map<T, @NotNull Integer> toPct(Map<T, @NotNull Integer> nbAdmis) {
        if(nbAdmis == null || nbAdmis.isEmpty()) return Map.of();
        int total = nbAdmis.values().stream().mapToInt(Integer::intValue).sum();
        if (total <= MIN_POPULATION_SIZE_FOR_STATS) return Map.of();
        return nbAdmis.entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        e -> (int) Math.round(100.0 * e.getValue() / total)
                ));
    }

    @NotNull
    public Map<String,  @NotNull Statistique> getStatsMoyGenParBac(String grp) {
        return getStatScolParBac(grp, MATIERE_MOYENNE_GENERALE_CODE);
    }


    public Map<String, @NotNull Statistique> getStatScolParBac(String groupe, int matiere) {
        @Nullable StatistiquesAdmisParBac toto = statsAdmis.parGroupe().get(groupe);
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


    public void ajouterMatiere(int iMtCod, String iMtLib) {
        matieres.put(iMtCod, iMtLib);
    }

    public void setStatistiquesAdmisFromPercentileCounters(
            Map<String, Map<String, Map<Integer, int[]>>> compteurs) {
        statsAdmis.clear();
        compteurs.forEach((gr, stringMapMap) ->
                statsAdmis.parGroupe()
                        .computeIfAbsent(gr, z -> new StatistiquesAdmisParBac())
                        .set(stringMapMap)
        );

    }

    public void ajouterStatistiqueAdmis(String grp, int specialite, int nb) {
        statsSpecialites.parGroupe()
                .computeIfAbsent(grp, z -> new TauxSpecialites())
                .parSpecialite().put(specialite, nb);
    }

    public void incrementeAdmisParFiliere(String g, String bac) {
        Map<String, Integer> m = admisParGroupes.computeIfAbsent(g, z -> new HashMap<>());
        m.put(bac , 1 + m.getOrDefault(bac, 0));
    }


    public void setAdmisParFiliereMatiere(String fl, int mtCod, int nb) {
        ajouterStatistiqueAdmis(fl, mtCod, nb);
    }

    public void minimize() {
        statsSpecialites.parGroupe().clear();
        admisParGroupes.clear();
        statsAdmis.minimize();
    }

    public static final int ANNEE_LYCEE_TERMINALE = 3;
    public static final int ANNEE_LYCEE_PREMIERE = 2;
    public void ajouterStatMatiereBac(int annLycee, int iMtCod, String iClCod, int nb) {
        if(nb > 100) {
            admisMatiereBacAnneeStats.stats().add(new AdmisMatiereBacAnnee(annLycee, iMtCod, iClCod, nb));
        }
    }


    public int getAnnee() {
        if(annee == null) throw new IllegalStateException("annee non initialisée");
        return this.annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }


    //trading cpu for memory

    public Set<String> getBacsWithAtLeastNdAdmis(int minNbAdmis) {
        @NotNull Map<@NotNull String, @NotNull Integer> admisBacsTousGroupes = admisParGroupes.getOrDefault(TOUS_GROUPES_CODE, Map.of());
        try {
            Serialisation.toJsonFile("statsBacs.json", admisBacsTousGroupes, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return admisBacsTousGroupes
                .entrySet()
                .stream().filter(e -> e.getValue() >= minNbAdmis)
                .map(Entry::getKey)
                .collect(Collectors.toSet());
    }

    public void restrictToBacs(Set<String> bacsActifs) {
        admisParGroupes.values().forEach(v -> v.keySet().retainAll(bacsActifs));
        admisMatiereBacAnneeStats.stats().removeIf(s -> !bacsActifs.contains(s.bac()));
        statsAdmis.parGroupe().values().forEach(s -> s.parBac().keySet().retainAll(bacsActifs));
        cleanup();
    }

    public void cleanup() {
        admisParGroupes.values().removeIf(Map::isEmpty);
        admisMatiereBacAnneeStats.stats().removeIf(s -> s.nb() == 0);
        statsAdmis.parGroupe().values().removeIf(s -> s.parBac().isEmpty());
    }

    public List<AdmisMatiereBacAnnee> getAdmisMatiereBacAnneeStats() {
        if(!admisMatiereBacAnneeStats.stats().isEmpty()) {
            return admisMatiereBacAnneeStats.stats();
        } else {//fallback en attendant de réparer la source de données
            return statsAdmis.parGroupe().values().stream()
                    .flatMap(grp -> grp.parBac().entrySet().stream()
                            .flatMap(bac -> bac.getValue().parMatiere().entrySet().stream()
                                    .map(mat -> new AdmisMatiereBacAnnee(
                                            ANNEE_LYCEE_TERMINALE,
                                            mat.getKey(),
                                            bac.getKey(),
                                            mat.getValue().nb()
                                    ))
                            )
                    )
                    .collect(Collectors.groupingBy(s -> Pair.of(s.iMtCod(), s.bac())))
                    .entrySet().stream()
                    .map(
                            e -> new AdmisMatiereBacAnnee(
                                    ANNEE_LYCEE_TERMINALE,
                                    e.getKey().getLeft(),
                                    e.getKey().getRight(),
                                    e.getValue().stream().mapToInt(AdmisMatiereBacAnnee::nb).sum()
                            )
                    )
                    .toList();
        }
    }

}
