package fr.gouv.monprojetsup.data.domain.model.stats;

import fr.gouv.monprojetsup.data.tools.Serialisation;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.domain.model.stats.StatistiquesAdmisParMatiere.getStatAgregee;


public class PsupStatistiques implements Serializable {

    public static final int MIN_POPULATION_SIZE_FOR_STATS = 10;
    public static final int SIM_FIL_MAX_WEIGHT = 100000;

    public static final String TOUS_GROUPES_CODE = "";
    public static final int MOYENNE_BAC_CODE = -2;

    public static final String TOUS_BACS_CODE_LEGACY = "";
    public static final String TOUS_BACS_CODE_MPS = "NC";
    public static final String TOUS_BACS_CODE_FRONT_LIBELLE = "Non-communiqué";

    public static final int PRECISION_PERCENTILES = 40;
    public static final int MOYENNE_GENERALE_CODE = -1;

    /* stats sur les spécialités des admis */
    private final TauxSpecialitesParGroupe statsSpecialites = new TauxSpecialitesParGroupe();

    /* stats sur les spécialités des candidats */
    private final TauxSpecialitesParGroupe statsSpecialitesCandidats = new TauxSpecialitesParGroupe();

    private final StatistiquesAdmisParGroupe statsAdmis = new StatistiquesAdmisParGroupe();

    //par groupe puis par bac
    private final @NotNull Map<String,@NotNull Map<@NotNull String, @NotNull Integer>> admisParGroupes = new TreeMap<>();

    //par groupe puis par bac
    private final @NotNull Map<String, @NotNull Map<@NotNull String,@NotNull Integer>> candidatsParGroupes = new TreeMap<>();

    public final @NotNull Map<@NotNull Integer, @NotNull String> matieres = new TreeMap<>();

    private final AdmisMatiereBacAnneeStats admisMatiereBacAnneeStats = new AdmisMatiereBacAnneeStats();

    private int annee = 2024;

    public @NotNull Map<String, @NotNull Integer> getNbAdmisParBac(String g) {
        return admisParGroupes.getOrDefault(g, Map.of());
    }


    public @Nullable Map<Integer, Integer> getNbAdmisParSpec(String g) {
        TauxSpecialites s = statsSpecialites.parGroupe().get(g);
        if(s == null) return null;
        return s.parSpecialite();
    }

    @Nullable
    public Map<@NotNull String, @NotNull Integer> getPctAdmisParBac(@NotNull String mpsKey) {
        return toPct(getNbAdmisParBac(mpsKey));
    }

    @Nullable
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
        return getStatScolParBac(grp, MOYENNE_GENERALE_CODE);
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

    public void ajouterStatistiqueCandidats(String grp, int specialite, int nb) {
        statsSpecialitesCandidats.parGroupe()
                .computeIfAbsent(grp, z -> new TauxSpecialites())
                .parSpecialite().put(specialite, nb);
    }

    public void incrementeAdmisParFiliere(String g, String bac) {
        Map<String, Integer> m = admisParGroupes.computeIfAbsent(g, z -> new HashMap<>());
        m.put(bac , 1 + m.getOrDefault(bac, 0));
    }

    public void incrementeCandidatsParFiliere(String g, String bac) {
        Map<String, Integer> m = candidatsParGroupes.computeIfAbsent(g, z -> new HashMap<>());
        m.put(bac , 1 + m.getOrDefault(bac, 0));
    }


    public void setAdmisParFiliereMatiere(String fl, int mtCod, int nb) {
        ajouterStatistiqueAdmis(fl, mtCod, nb);
    }

    public void setCandidatsParFiliereMatiere(String fl, int mtCod, int nb) {
        ajouterStatistiqueCandidats(fl, mtCod, nb);
    }

    public void minimize() {
        statsSpecialites.parGroupe().clear();
        statsSpecialitesCandidats.parGroupe().clear();
        admisParGroupes.clear();
        statsAdmis.minimize();
        candidatsParGroupes.clear();
    }

    public static final int ANNEE_LYCEE_TERMINALE = 3;
    public static final int ANNEE_LYCEE_PREMIERE = 2;
    public void ajouterStatMatiereBac(int annLycee, int iMtCod, String iClCod, int nb) {
        if(nb > 100) {
            admisMatiereBacAnneeStats.stats().add(new AdmisMatiereBacAnnee(annLycee, iMtCod, iClCod, nb));
        }
    }


    public void createGroupAdmisStatistique(@NotNull Map<String, String> flGroups) {
        Map<String, Set<String>> reverseFlGroups = new HashMap<>();
        flGroups.forEach((s, s2) -> reverseFlGroups.computeIfAbsent(s2, z -> new HashSet<>()).add(s));
        reverseFlGroups.forEach(this::createGroupAdmisStatistique);
    }


    private void createGroupAdmisStatistique(
            String newGroup,
            Set<String> groups,
            Map<String, Map<String,Integer>> parGroupeParBac
    ) {
        Map<String, Integer> statAgregee = parGroupeParBac.entrySet().stream()
                .filter(e -> groups.contains(e.getKey()))
                .map(Entry::getValue)
                .flatMap(e -> e.entrySet().stream())
                .collect(Collectors.groupingBy(Entry::getKey))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        e -> e.getValue().stream().mapToInt(Entry::getValue).sum())
                );
        parGroupeParBac.put(newGroup, statAgregee);
    }

    private void createGroupAdmisStatistique(String newGroup, Set<String> groups, TauxSpecialitesParGroupe stats) {
        Map<Integer, Integer> statAgregee = stats.parGroupe().entrySet().stream()
                .filter(e -> groups.contains(e.getKey()))
                .map(Entry::getValue)
                .flatMap(e -> e.parSpecialite().entrySet().stream())
                .collect(Collectors.groupingBy(Entry::getKey))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        e -> e.getValue().stream().mapToInt(Entry::getValue).sum())
                );
        stats.parGroupe().put(
                newGroup,
                new TauxSpecialites(statAgregee)
        );
    }
    private void createGroupAdmisStatistique(
            String newGroup,
            Set<String> groups,
            StatistiquesAdmisParGroupe statsAdmis
    ) {
        Map<String, StatistiquesAdmisParMatiere> statAgregee
                = statsAdmis.parGroupe().entrySet().stream()
                .filter(e -> groups.contains(e.getKey()))
                .flatMap(e -> e.getValue().parBac().entrySet().stream())
                .collect(Collectors.groupingBy(Entry::getKey))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        e -> getStatAgregee(e.getValue().stream().map(Entry::getValue).toList())
                ));

        statsAdmis.parGroupe().put(newGroup, new StatistiquesAdmisParBac(statAgregee));
    }


    public void createGroupAdmisStatistique(String newGroup, Set<String> groups) {

        createGroupAdmisStatistique(newGroup, groups, admisParGroupes);
        createGroupAdmisStatistique(newGroup, groups, candidatsParGroupes);
        createGroupAdmisStatistique(newGroup, groups, statsSpecialites);
        createGroupAdmisStatistique(newGroup, groups, statsSpecialitesCandidats);
        createGroupAdmisStatistique(newGroup, groups, statsAdmis);


    }

    public void rebuildMiddle50() {
        /* reconstruction des middle 50 */
        statsAdmis.parGroupe().values().stream()
                .flatMap(e -> e.parBac().values().stream())
                .flatMap(s -> s.parMatiere().entrySet().stream())
                .forEach(e -> e.setValue(e.getValue().updateMiddle50FromFrequencesCumulees()));
    }


    public int getAnnee() {
        return this.annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }


    //trading cpu for memory
    private final transient ConcurrentHashMap<Triple<String,String, Boolean>, Map<Integer, StatFront>> statsCache = new ConcurrentHashMap<>();

    private Map<Integer, StatFront> computeStatsScol(String groupe, String bac, boolean minimalForStudent) {
        StatistiquesAdmisParBac sapb = statsAdmis.parGroupe().get(groupe);
        if(sapb == null) return Collections.emptyMap();
        StatistiquesAdmisParMatiere st = sapb.parBac().get(bac);
        if(st == null) return Collections.emptyMap();
        Map<Integer, Statistique> result = st.parMatiere();
        result.entrySet().removeIf(e -> (minimalForStudent && e.getKey() != MOYENNE_GENERALE_CODE) || e.getValue().nb() < 5);
        return result.entrySet().stream().collect(Collectors.toMap(
                Entry::getKey,
                e -> StatFront.getStatistique(e.getValue().frequencesCumulees(), minimalForStudent)
        ));
    }

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
        candidatsParGroupes.values().forEach(v -> v.keySet().retainAll(bacsActifs));
        admisMatiereBacAnneeStats.stats().removeIf(s -> !bacsActifs.contains(s.bac()));
        statsAdmis.parGroupe().values().forEach(s -> s.parBac().keySet().retainAll(bacsActifs));
        cleanup();
    }

    public void cleanup() {
        admisParGroupes.values().removeIf(Map::isEmpty);
        candidatsParGroupes.values().removeIf(Map::isEmpty);
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
