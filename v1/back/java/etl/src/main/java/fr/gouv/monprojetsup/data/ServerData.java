package fr.gouv.monprojetsup.data;

import fr.gouv.monprojetsup.data.config.DataServerConfig;
import fr.gouv.monprojetsup.data.distances.Distances;
import fr.gouv.monprojetsup.data.model.cities.CitiesBack;
import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.data.model.formations.Formation;
import fr.gouv.monprojetsup.data.model.specialites.Specialites;
import fr.gouv.monprojetsup.data.model.specialites.SpecialitesLoader;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.model.stats.StatsContainers;
import fr.gouv.monprojetsup.data.model.tags.TagsSources;
import fr.gouv.monprojetsup.data.update.BackEndData;
import fr.gouv.monprojetsup.data.update.UpdateFrontData;
import fr.gouv.monprojetsup.data.update.onisep.DomainePro;
import fr.gouv.monprojetsup.data.update.onisep.OnisepData;
import fr.gouv.monprojetsup.data.update.psup.PsupData;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.parcoursup.carte.modele.modele.JsonCarte;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.common.server.Helpers.LOGGER;
import static fr.gouv.monprojetsup.data.Constants.*;
import static fr.parcoursup.carte.algos.Filiere.LAS_CONSTANT;


@Slf4j
public class ServerData {


    public static final String GROUPE_INFIX = " groupe ";
    /***************************************************************************
     ******************* DATAS ***********************************
     ****************************************************************************/

    public static PsupData backPsupData;
    public static PsupStatistiques statistiques;
    public static OnisepData onisepData;
    public static JsonCarte carte;

    /* la liste des filières groupées visibles dans le front.
    * Définit le périmètre des résultats de recherche et des recommandations remontées au front.
    * Contient toutes les filières actives + les las - les filières groupées + les groupements de filières
    * */
    public static final Set<String> filieresFront = new HashSet<>();

    public static TagsSources tagsSources;

    public static Map<String, Set<String>> liensSecteursMetiers;
    public static Map<DomainePro, Set<String>> liensDomainesMetiers;


    public static final Map<String, Set<String>> reverseFlGroups = new HashMap<>();

    public static Specialites specialites;

    //regroupement des filieres
    public static Map<String, String> flGroups = null;

    protected static final Map<String, List<Formation>> filToFormations = new HashMap<>();
    protected static final Map<String, String> formationsToFilieres = new HashMap<>();

    public static CitiesBack cities = null;
    /*
    ************************************************************************
    **************************** LOADERS ***********************************
    ************************************************************************
     */

    private static boolean dataLoaded = false;
    private static final Map<String, Integer> nbFormations = new HashMap<>();
    private static final Map<String, Integer> capacity = new HashMap<>();
    public static int p90NbFormations;
    public static int p50Capacity;
    public static int p75Capacity;
    public static int p90Capacity;


    /**
     * Load data into server
     * @throws IOException unlucky
     */
    public static synchronized void load() throws IOException {

        if (dataLoaded) return;

        DataServerConfig.load();

        log.info("Loading server data...");

        loadBackEndData();
        loadStatistiques();

        backPsupData.filActives().addAll(statistiques.getLasFlCodes());
        flGroups = new HashMap<>(backPsupData.getCorrespondances());
        flGroups.forEach((s, s2) -> reverseFlGroups.computeIfAbsent(s2, z -> new HashSet<>()).add(s));
        reverseFlGroups.forEach((key, value) -> {
            if(value.size() > 1) {
                filToFormations.put(key, value.stream().flatMap(f -> filToFormations.getOrDefault(f, List.of()).stream()).collect(Collectors.toList()));
            }
        });


        ServerData.specialites = SpecialitesLoader.load(ServerData.statistiques);

        /* can be deleted afte rnext data update */
        ServerData.statistiques.removeSmallPopulations();
        ServerData.statistiques.rebuildMiddle50();
        ServerData.statistiques.createGroupAdmisStatistique(reverseFlGroups);
        ServerData.statistiques.createGroupAdmisStatistique(getLasToGtaMapping());

        ServerData.updateLabelsForDebug();

        liensSecteursMetiers = OnisepData.getSecteursVersMetiers(
                onisepData.fichesMetiers(),
                onisepData.formations().getFormationsDuSup()
        );
        liensDomainesMetiers = OnisepData.getDomainesVersMetiers(onisepData.metiers());


        Distances.init();

        computeFilieresFront();

        for (String s : filieresFront) {
            if (filToFormations.getOrDefault(s, List.of()).isEmpty())
                LOGGER.warning("No formations for " + s);
                //throw new RuntimeException("No formations for " + s);
        }

        filToFormations.forEach((key, value) -> {
            nbFormations.put(key, value.size());
            capacity.put(key, value.stream().mapToInt(f -> f.capacite).sum());
        });
        p90NbFormations
                = p50(nbFormations.entrySet().stream().filter(e -> filieresFront.contains(e.getKey())).map(Map.Entry::getValue).toList());
        p50Capacity = p50(capacity.entrySet().stream().filter(e -> filieresFront.contains(e.getKey())).map(Map.Entry::getValue).toList());
        p75Capacity = p75(capacity.entrySet().stream().filter(e -> filieresFront.contains(e.getKey())).map(Map.Entry::getValue).toList());
        p90Capacity = p90(capacity.entrySet().stream().filter(e -> filieresFront.contains(e.getKey())).map(Map.Entry::getValue).toList());

        initTagSources();

        dataLoaded = true;
    }

    private static Integer p25(Collection<Integer> values) {
        return values.stream().sorted().skip(values.size() / 4).findFirst().orElse(0);
    }
    private static Integer p50(Collection<Integer> values) {
        return values.stream().sorted().skip(values.size() / 2).findFirst().orElse(0);
    }
    private static Integer p75(Collection<Integer> values) {
        return values.stream().sorted().skip(75L * values.size() / 100).findFirst().orElse(0);
    }
    private static Integer p90(Collection<Integer> values) {
        return values.stream().sorted().skip(90L * values.size()  /100).findFirst().orElse(0);
    }

    private static void computeFilieresFront() {
        filieresFront.addAll(backPsupData.filActives().stream().map(Constants::gFlCodToFrontId).toList());
        filieresFront.removeAll(flGroups.keySet());
        filieresFront.addAll(flGroups.values());
    }

    private static void initTagSources() throws IOException {
        tagsSources = TagsSources.load(backPsupData.getCorrespondances());
        val metiersVersFormations = getMetiersVersFormations();
        filieresFront.forEach(filiere -> {
            String label = getLabel(filiere);
            if(label !=  null) {
                tagsSources.add(label, filiere);
                if (label.contains("L1")) {
                    tagsSources.add("licence", filiere);
                }
                if (label.toLowerCase().contains("infirmier")) {
                    tagsSources.add("IFSI", filiere);
                }
            } else {
                LOGGER.warning("Excluding label in search for  " + filiere + " since it has no label");
            }
            getMetiersAssocies(filiere, metiersVersFormations).forEach(
                    metier -> {
                        tagsSources.add(getLabel(metier), filiere);
                    });
        });

        tagsSources.normalize();

        Serialisation.toJsonFile("tagsSources.json", tagsSources, true);

    }

    private static Collection<String> getMetiersAssocies(String filiere, Map<String, Set<String>> metiersVersFormations) {
        Set<String> result = new HashSet<>();
        result.addAll(metiersVersFormations.entrySet().stream().filter(e -> e.getValue().contains(filiere)).map(Map.Entry::getKey).collect(Collectors.toSet()));
        if(statistiques.getLASCorrespondance().isLas(filiere)) {
            result.addAll(getMetiersAssocies(gFlCodToFrontId(PASS_FL_COD), metiersVersFormations));
        }
        if(reverseFlGroups.containsKey(filiere)) {
            result.addAll(reverseFlGroups.get(filiere)
                    .stream()
                    .filter(f -> f != filiere)
                    .flatMap(f -> getMetiersAssocies(f, metiersVersFormations).stream()).collect(Collectors.toSet()));
        }
        return result;
    }

    public static @NotNull Map<Pair<String,String>,List<String>> getAttendusParGroupes() {
        Map<Pair<String,String>, List<String>> result = new HashMap<>();
        backPsupData.filActives().forEach(gFlCod -> {
            String key = Constants.gFlCodToFrontId(gFlCod);
            String label = getDebugLabel(key);
            if(label != null) {
                val attendus = backPsupData.getAttendus(gFlCod);
                val recoPrem = backPsupData.getRecoPremGeneriques(gFlCod);
                val recoTerm = backPsupData.getRecoTermGeneriques(gFlCod);
                if(attendus != null) {
                    val p = Pair.of("attendus", attendus);
                    result.computeIfAbsent(p, z -> new ArrayList<>()).add(label);
                }
                if(recoPrem != null) {
                    val p = Pair.of("recoPrem", recoPrem);
                    result.computeIfAbsent(p, z -> new ArrayList<>()).add(label);
                }
                if(recoTerm != null) {
                    val p = Pair.of("recoTerm", recoTerm);
                    result.computeIfAbsent(p, z -> new ArrayList<>()).add(label);
                }
            }
        });
        return result;
    }
    private static void loadBackEndData() throws IOException {

        BackEndData backendData = Serialisation.fromZippedJson(DataSources.getBackDataFilePath(), BackEndData.class);

        ServerData.onisepData = backendData.onisepData();
        ServerData.carte = backendData.carte();

        backPsupData = backendData.psupData();
        backPsupData.cleanup();//should be useless but it does not harm...

        val groupes = backPsupData.getCorrespondances();

        backPsupData.formations().formations.values()
                .forEach(f -> {
                    int gFlCod = (f.isLAS() && f.gFlCod < LAS_CONSTANT) ? f.gFlCod + LAS_CONSTANT: f.gFlCod;
                    String filKey = Constants.gFlCodToFrontId(gFlCod);
                    String forKey = Constants.gTaCodToFrontId(f.gTaCod);
                    filToFormations
                            .computeIfAbsent(filKey, z -> new ArrayList<>())
                            .add(f);
                    val grKey = groupes.get(filKey);
                    if(Objects.nonNull(grKey)) {
                        filToFormations
                                .computeIfAbsent(grKey, z -> new ArrayList<>())
                                .add(f);
                        formationsToFilieres.put(forKey, grKey);
                    } else {
                        formationsToFilieres.put(forKey, filKey);
                    }
                });

        ServerData.cities = new CitiesBack(backendData.cities().cities());

    }

    private static void loadStatistiques() throws IOException {
        ServerData.statistiques = Serialisation.fromZippedJson(DataSources.getSourceDataFilePath(DataSources.STATS_BACK_SRC_FILENAME), PsupStatistiques.class);
    }

    private static Map<String, Set<String>> getLasToGtaMapping() {
        //fl1002033
        Set<String> lasCodes = ServerData.statistiques.getLASCorrespondance().lasToGeneric().keySet();
        return
                lasCodes
                        .stream()
                        .collect(Collectors.toMap(
                                        las -> las,
                                        las -> filToFormations.getOrDefault(las, List.of())
                                                .stream()
                                                .map(f ->  FORMATION_PREFIX + f.gTaCod)
                                                .collect(Collectors.toSet())
                                )
                        );
    }

    public static String getGroupOfFiliere(String fl) {
        return flGroups.getOrDefault(fl,fl);
    }
    public static Set<String> getFilieresOfGroup(String fl) {
        return  reverseFlGroups.getOrDefault(fl, Set.of(fl));
    }

    private static void updateLabelsForDebug() {
        statistiques.updateLabels(onisepData, backPsupData, statistiques.getLASCorrespondance().lasToGeneric());
        try {
            Serialisation.toJsonFile("labelsNoDebug.json", statistiques.labels, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> suffixes =
                reverseFlGroups.entrySet().stream()
                        .filter(e -> !e.getValue().isEmpty())
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        s -> s.getValue().toString())
                                );
        suffixes.forEach((key, suffix) -> statistiques.labels.put(key, statistiques.labels.get(key) + GROUPE_INFIX + suffix) );
        try {
            Serialisation.toJsonFile("labelsDebug.json", statistiques.labels, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /* ************************************************************************
    ************************* HELPERS to get labels associated with a key ***********************
     */
    public static String getLabel(String key) {
        return getLabel(key, ServerData.statistiques.nomsFilieres.get(key));
    }

    public static String getDebugLabel(String key) {
        return getLabel(key) + " (" + key  + ")";
    }

    public static String getLabel(String key, String defaultValue) {
        return ServerData.statistiques.labels.getOrDefault(key, defaultValue);
    }


    /*
    ***********************************************
    ************* STATS HELPERS *******************
    ************************************************/

    /**
     * utilisé pour l'envoi des stats aux profs
     * @param bac le bac
     * @param groups les groupes
     * @return les stats
     */
    public static Map<String, StatsContainers.DetailFiliere> getGroupStats(@Nullable String bac, @Nullable Collection<String> groups) {
        if(groups == null) return Collections.emptyMap();
        if(bac == null) bac = PsupStatistiques.TOUS_BACS_CODE;
        @Nullable String finalBac = bac;
        return groups.stream().collect(Collectors.toMap(
                g -> g,
                g -> getDetailedGroupStats(finalBac, g)
        ));
    }

    /**
     * utilisé pour l'envoi des stats aux élèves
     *
     * @param bac le bac
     * @param g le groupe
     * @return les détails
     */
    public static @NotNull StatsContainers.SimpleStatGroupParBac getSimpleGroupStats(@Nullable String bac, String g) {
        if(bac == null) bac = PsupStatistiques.TOUS_BACS_CODE;
        return getDetailedGroupStats(bac, g, false).stat();
    }

    /**
     * utilisé pour l'envoi des stats aux profs
     * @param bac le bac
     * @param g le groupe
     * @return les stats
     */
    private static StatsContainers.DetailFiliere getDetailedGroupStats(@NotNull String bac, String g) {
        return getDetailedGroupStats(bac, g, true);
    }
    private static StatsContainers.DetailFiliere getDetailedGroupStats(@NotNull String bac, String g, boolean includeProfDetails) {
        StatsContainers.SimpleStatGroupParBac statFil
                = new StatsContainers.SimpleStatGroupParBac(
                        statistiques.getGroupStats(
                                g,
                                bac,
                                !includeProfDetails
                        )
        );
        //statFil.stats().entrySet().removeIf(e -> e.getValue().nbAdmis() == null);

        if(includeProfDetails) {
            Map<String, StatsContainers.DetailFormation> statsFormations = new HashMap<>();
            try {
                List<Formation> fors = getFormationsFromFil(g);
                fors.forEach(f -> {
                    try {
                        String fr = FORMATION_PREFIX + f.gTaCod;
                        StatsContainers.SimpleStatGroupParBac statFor = new StatsContainers.SimpleStatGroupParBac(
                                statistiques.getGroupStats(
                                        fr,
                                        bac,
                                        !includeProfDetails)
                        );
                        statFor.stats().entrySet().removeIf(e -> e.getValue().statsScol().isEmpty());
                        statsFormations.put(fr, new StatsContainers.DetailFormation(
                                f.libelle,
                                fr,
                                statFor
                        ));
                    } catch (Exception ignored) {
                        //ignored
                    }
                });
            } catch (Exception ignored) {
                //ignore
            }
            return new StatsContainers.DetailFiliere(
                    g,
                    statFil,
                    statsFormations
            );
        } else {
            return new StatsContainers.DetailFiliere(
                    g,
                    statFil,
                    null
            );
        }
    }

    /**
     * Get the list of formations for a filiere. Used by the suggestion service to compute foi (formations of interest).
     * @param fl the filiere
     * @return the list of formations
     */
    public static List<Formation> getFormationsFromFil(String fl) {
        return filToFormations
                .getOrDefault(fl, Collections.emptyList());
    }

    private ServerData() {

    }

    public static Map<String, Integer> search(String searchString) {

        List<String> tags = Arrays.stream(
                searchString.replaceAll("[-,_()]", " ").split(" ")
        )
                .map(String::toLowerCase)
                .map(w -> Normalizer.normalize(w, Normalizer.Form.NFD))
                .filter(s -> !s.isBlank()).toList();

        return tagsSources.getScores(tags, filieresFront);

    }

    public static Map<String, Set<String>> getMetiersVersFormations() throws IOException {
        Descriptifs descriptifs = UpdateFrontData.DataContainer.loadDescriptifs(
                onisepData,
                backPsupData.getCorrespondances(),
                statistiques.getLASCorrespondance().lasToGeneric()
        );

        return onisepData.getExtendedMetiersVersFormations(
                backPsupData.getCorrespondances(),
                statistiques.getLASCorrespondance(),
                descriptifs
        );

    }

    public static Set<String> getMetiersPass(Map<String, Set<String>> metiersVersFormation) throws IOException {
        String passKey =  Constants.gFlCodToFrontId(PASS_FL_COD);

        Set<String> metiersPass = metiersVersFormation.entrySet().stream()
                .filter(e -> e.getValue().contains( passKey))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        return metiersPass;
    }


    public static int getNbFormations(String fl) {
        return nbFormations.getOrDefault(fl, 0);
    }

    public static int getCapacity(String fl) {
        return capacity.getOrDefault(fl, 0);
    }

    public static Map<String, String> getFormationTofilieres() {
        return new HashMap<>(formationsToFilieres);
    }

}

