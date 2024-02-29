package fr.gouv.monprojetsup.data;

import fr.gouv.monprojetsup.data.model.cities.CitiesBack;
import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.data.model.eds.Attendus;
import fr.gouv.monprojetsup.data.model.eds.EDSAggAnalysis;
import fr.gouv.monprojetsup.data.model.eds.EDSAnalysis;
import fr.gouv.monprojetsup.data.model.formations.FilieresToFormationsOnisep;
import fr.gouv.monprojetsup.data.model.formations.Formation;
import fr.gouv.monprojetsup.data.model.interets.Interets;
import fr.gouv.monprojetsup.data.model.specialites.Specialites;
import fr.gouv.monprojetsup.data.model.specialites.SpecialitesLoader;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.model.stats.Statistique;
import fr.gouv.monprojetsup.data.model.stats.StatsContainers;
import fr.gouv.monprojetsup.data.model.tags.TagsSources;
import fr.gouv.monprojetsup.data.update.BackEndData;
import fr.gouv.monprojetsup.data.update.UpdateFrontData;
import fr.gouv.monprojetsup.data.update.onisep.DomainePro;
import fr.gouv.monprojetsup.data.update.onisep.LienFormationMetier2;
import fr.gouv.monprojetsup.data.update.onisep.OnisepData;
import fr.gouv.monprojetsup.data.update.onisep.billy.PsupToOnisepLines;
import fr.gouv.monprojetsup.data.update.onisep.formations.Formations;
import fr.gouv.monprojetsup.data.update.psup.PsupData;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.algos.Explanation;
import fr.gouv.monprojetsup.tools.Serialisation;
import fr.gouv.monprojetsup.tools.csv.CsvTools;
import fr.gouv.monprojetsup.web.server.WebServer;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.FORMATION_PREFIX;
import static fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions.*;
import static fr.parcoursup.carte.algos.Filiere.LAS_CONSTANT;


public class ServerData {


    /***************************************************************************
     ******************* DATAS ***********************************
     ****************************************************************************/

    public static PsupData backPsupData;
    public static PsupStatistiques statistiques;
    public static OnisepData onisepData;

    public static Map<String, Set<String>> liensSecteursMetiers;
    public static Map<DomainePro, Set<String>> liensDomainesMetiers;


    public static final Map<String, Set<String>> reverseFlGroups = new HashMap<>();

    public static Specialites specialites;
    public static Map<String, Integer> codesSpecialites = new HashMap<>();

    //regroupement des filieres
    public static Map<String, String> flGroups = null;

    protected static final Map<String, List<Formation>> filToFormations = new HashMap<>();

    public static CitiesBack cities = null;
    /*
    ************************************************************************
    **************************** LOADERS ***********************************
    ************************************************************************
     */

    private static boolean dataLoaded = false;


    /**
     * Load data into server
     * @throws IOException unlucky
     */
    public static synchronized void load() throws IOException {
        load(null);
    }
    public static synchronized void load(@Nullable Path rootDataDir) throws IOException {

        if(dataLoaded) return;

        LOGGER.info("Loading server data...");

        loadBackEndData();

        flGroups = new HashMap<>(backPsupData.getCorrespondances());
        flGroups.forEach((s, s2) -> reverseFlGroups.computeIfAbsent(s2, z -> new HashSet<>()).add(s));

        ServerData.specialites = SpecialitesLoader.load();
        ServerData.specialites.specialites().forEach((iMtCod, s) -> ServerData.codesSpecialites.put(s, iMtCod));

        ServerData.statistiques = Serialisation.fromZippedJson(DataSources.getSourceDataFilePath(DataSources.STATS_BACK_SRC_FILENAME), PsupStatistiques.class);
        ServerData.statistiques.removeSmallPopulations();

        ServerData.statistiques =
                Serialisation.fromZippedJson(
                        DataSources.getSourceDataFilePath(DataSources.STATS_BACK_SRC_FILENAME),
                        PsupStatistiques.class
                );
        /* can be deleted afte rnext data update */
        ServerData.statistiques.rebuildMiddle50();
        ServerData.statistiques.createGroupAdmisStatistique(reverseFlGroups);
        ServerData.statistiques.createGroupAdmisStatistique(getLasToGtaMapping());

        ServerData.updateLabelsForDebug();

        liensSecteursMetiers  = OnisepData.getSecteursVersMetiers(onisepData.fichesMetiers());
        liensDomainesMetiers  = OnisepData.getDomainesVersMetiers(onisepData.metiers());

        dataLoaded = true;
    }

    private static Map<String, Set<String>> getLasToGtaMapping() {
        //fl1002033
        Set<String> lasCodes = ServerData.statistiques.getLASCorrespondance().lasToGeneric().keySet();
        Map<String, Set<String>> result =
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
        return result;
    }

    public static String getGroupOfFiliere(String fl) {
        return flGroups.getOrDefault(fl,fl);
    }
    public static Set<String> getFilieresOfGroup(String fl) {
        return  reverseFlGroups.getOrDefault(fl, Set.of(fl));
    }

    private static void updateLabelsForDebug() {
        statistiques.updateLabels(onisepData, backPsupData, statistiques.getLASCorrespondance().lasToGeneric());
        Map<String, String> suffixes =
                reverseFlGroups.entrySet().stream()
                        .filter(e -> !e.getValue().isEmpty())
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        s -> s.getValue().toString())
                                );
        suffixes.forEach((key, suffix) -> statistiques.labels.put(key, statistiques.labels.get(key) + " groupe " + suffix) );
        try {
            Serialisation.toJsonFile("labelsDebug.json", statistiques.labels, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean isFiliere(@NotNull String key) {
        return key.startsWith(Constants.FILIERE_PREFIX)
                || key.startsWith(Constants.TYPE_FORMATION_PREFIX);
    }
    public static boolean isMetier(@NotNull String key) {
        return key.startsWith(Constants.MET_PREFIX);
    }

    public static boolean isTheme(@NotNull String key) {
        return key.startsWith(Constants.THEME_PREFIX);
    }

    static void loadBackEndData() throws IOException {

        BackEndData backendData = Serialisation.fromZippedJson(DataSources.getBackDataFilePath(), BackEndData.class);

        ServerData.onisepData = backendData.onisepData();

        backPsupData = backendData.psupData();
        backPsupData.cleanup();//should be useless but it does not harm...

        backPsupData.formations().formations.values()
                .forEach(f -> {
                    int gFlCod = (f.isLAS() && f.gFlCod < LAS_CONSTANT) ? f.gFlCod + LAS_CONSTANT: f.gFlCod;
                    filToFormations
                            .computeIfAbsent(Constants.gFlCodToFrontId(gFlCod), z -> new ArrayList<>())
                            .add(f);
                });

        ServerData.cities = new CitiesBack(backendData.cities().cities());

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
    Utilisé pour aire des stats
     */
    private static Pair<Triple<Double,Double,Double>, Triple<Double,Double,Double>> getPaireMoyenne(String key) {
        Pair<String, Statistique> statsMoyGen = statistiques.getStatsMoyGen(key, PsupStatistiques.TOUS_BACS_CODE);
        Pair<String,Statistique> statsBac = statistiques.getStatsBac(key, PsupStatistiques.TOUS_BACS_CODE);
        Triple<Double,Double,Double> moyGen = statsMoyGen == null || statsMoyGen.getRight() == null || statsMoyGen.getRight().nb() < 200 ? null :
                statsMoyGen.getRight().middle50().getTriple();
        Triple<Double,Double,Double> moyBac = statsBac == null || statsBac.getRight() == null || statsBac.getRight().nb() < 200 ? null :
                statsBac.getRight().middle50().getTriple();
        return Pair.of(
                moyGen,
                moyBac
        );
    }


    /**
     * utilisé pour l'envoi des stats aux élèves
     *
     * @param bac le bac
     * @param g le groupe
     * @return les détails
     */
    public static @NotNull StatsContainers.DetailFiliere getSimpleGroupStats(@Nullable String bac, String g) {
        if(bac == null) bac = PsupStatistiques.TOUS_BACS_CODE;
        return getDetailedGroupStats(bac, g, false);
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

    public static List<Formation> getFormationsFromFil(String fl) {
        return filToFormations
                .getOrDefault(fl, Collections.emptyList());
    }

    private ServerData() {

    }

    public static void main(String[] args) throws Exception {

        WebServer.loadConfig();

        ServerData.load();

        AlgoSuggestions.initialize();

        Serialisation.toJsonFile("relatedToHealth.json",
        AlgoSuggestions.getRelatedToHealth()
                .stream().map(s -> ServerData.getDebugLabel(s))
                        .toList()
        , true);

        exportLiensDomainesMetiers();

        exportCentresDinterets();

        Serialisation.toJsonFile("thematiques.json", ServerData.onisepData.thematiques().thematiques(), true);

        /**** items sans descriptis  ****/
        Descriptifs desc = UpdateFrontData.DataContainer.loadDescriptifs(ServerData.onisepData, flGroups, statistiques.getLASCorrespondance().lasToGeneric());
        Set<Integer> lasses = backPsupData.las();
        List<String> filieresAffichees = ServerData.backPsupData.filActives().stream()
                .filter(f -> !lasses.contains(f))
                .map(Constants::gFlCodToFrontId)
                .map(f -> flGroups.getOrDefault(f,f))
                .distinct()
                .sorted(Comparator.comparing(f -> ServerData.getLabel(f,f)))
                .toList();
        try(OutputStreamWriter out = new OutputStreamWriter(
                new BufferedOutputStream(Files.newOutputStream(Path.of("resumes.html")))
                , StandardCharsets.UTF_8)) {
            out.write(
                    """
                            <h1>Propositions de résumés de descriptifs de formations</h1>
                            <p>Ce document présente des propositions de résumés de descriptifs de formation récupérés depuis le site Onisep.fr.</p>
                            <p>La génération des résumés est effectuée à l'aide d'une IA générative,
                            en lui demandant de réduire les textes originaux à moins de 500 caractères.</p>
                            <p>Il y a eu une vérification humaine sur quelques exemples, mais pas systématique.</p>
                            """
            );

            int index = 1;
            for (String key : filieresAffichees) {
                Descriptifs.Descriptif descriptif = desc.keyToDescriptifs().get(key);
                String label = ServerData.getLabel(key);
                if (descriptif != null && descriptif.summary() != null && !label.contains("LAS")) {
                    out.write("<hr/>" + System.lineSeparator());
                    out.write("<h2>" + label + "</h2>");
                    out.write("<br/>" + System.lineSeparator());
                    out.write("<br/>" + System.lineSeparator());
                    out.write("<br/>" + System.lineSeparator());
                    out.write("<h3>Original</h3><p>" + descriptif.presentation().length() + " caractères</p>");
                    out.write("<br/>" + System.lineSeparator());
                    out.write("<br/>" + System.lineSeparator());
                    out.write(descriptif.presentation());
                    out.write("<br/>" + System.lineSeparator());
                    out.write("<br/>" + System.lineSeparator());
                    out.write("<br/>" + System.lineSeparator());
                    out.write("<h3>Résumé</h3><p>" + descriptif.summary().length() + " caractères</p>");
                    out.write("<br/>" + System.lineSeparator());
                    out.write("<br/>" + System.lineSeparator());
                    out.write(descriptif.summary());
                    out.write("<br/>" + System.lineSeparator());
                    out.write("<br/>" + System.lineSeparator());
                    out.write("<br/>" + System.lineSeparator());
                    out.write("<br/>" + System.lineSeparator());
                    index++;
                }

            }
            out.write("Total: " + index + " résumés produits.");
        }


        comparerMoyennesBacsEtSco();

        Map<String, Set<String>> regroupements = new TreeMap<>();
        flGroups.forEach((s, s2) -> {
            String l = ServerData.getLabel(s, s) + " (" + s + ")";
            String l2 = ServerData.getLabel(s2, s2) + " (" + s2 + ")";
            Set<String> set = regroupements.computeIfAbsent(l2, z -> new TreeSet<>());
            set.add(l);
            if (ServerData.statistiques.nomsFilieres.containsKey(s2)) {
                set.add(l2);
            }
        });
        Serialisation.toJsonFile("regroupements.json", regroupements, true);

        AlgoSuggestions.createGraph();
        edgesLabels.createLabelledGraphFrom(edgesKeys, ServerData.statistiques.labels);

        Serialisation.toJsonFile("semantic_graph.json", edgesLabels, true);

        Map<String, String> formationsSansMetiers = edgesKeys.edges().entrySet().stream()
                .filter(e -> isFiliere(e.getKey()))
                .filter(e -> e.getValue().stream().noneMatch(ServerData::isMetier))
                .map(e -> Pair.of(e.getKey(), ServerData.getDebugLabel(e.getKey())))
                .filter(e -> !e.getRight().contains("groupe") && !e.getRight().contains("null"))
                .collect(Collectors.toMap(
                        Pair::getLeft,
                        Pair::getRight
                ));
        Serialisation.toJsonFile("formations_sans_metiers.json",
                formationsSansMetiers.values(),
                true);

        Map<String, String> billys = onisepData.billy().psupToIdeo2().stream()
                .collect(Collectors.toMap(
                        PsupToOnisepLines.PsupToOnisepLine::G_FL_COD,
                        e -> e.IDS_IDEO2()
                ));

        try(CsvTools csv = new CsvTools("formations_psup_sans_metiers.csv",',')) {
            csv.append(List.of("id", "LIS_ID_ONI2","label"));
            formationsSansMetiers.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> {
                try {
                    csv.append(e.getKey());
                    csv.append(billys.getOrDefault(e.getKey(),""));
                    csv.append(e.getValue().substring(0, e.getValue().indexOf("(f")));
                    csv.newLine();
                } catch (IOException f) {
                    throw new RuntimeException(f);
                }
            });
        }

        Map<String, String> metiersSansFormations = edgesKeys.edges().entrySet().stream()
                .filter(e -> isMetier(e.getKey()))
                .filter(e -> e.getValue().stream().noneMatch(f -> isFiliere(f)))
                .map(e -> Pair.of(e.getKey(), ServerData.getDebugLabel(e.getKey())))
                .filter(e -> !e.getRight().contains("null"))
                .collect(Collectors.toMap(
                        Pair::getLeft,
                        Pair::getRight
                ));

        Serialisation.toJsonFile("metiers_sans_formations_psup.json",
                metiersSansFormations.values(),
                true);

        try(CsvTools csv = new CsvTools("metiers_sans_formations.csv",',')) {
            csv.append(List.of("id", "label"));
            metiersSansFormations.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> {
                try {
                    csv.append(e.getKey().replace("_","."));
                    csv.append(e.getValue().substring(0,e.getValue().indexOf("(MET")));
                    csv.newLine();
                } catch (IOException f) {
                    throw new RuntimeException(f);
                }
            });
        }


        Serialisation.toJsonFile("formations_sans_themes.json",
                AlgoSuggestions.edgesKeys.edges().entrySet().stream()
                        .filter(e -> isFiliere(e.getKey()))
                        .filter(e -> e.getValue().stream().noneMatch(f -> isTheme(f)))
                        .map(e -> ServerData.getDebugLabel(e.getKey()))
                        .filter(e -> !e.contains("groupe"))
                        .toList(),
                true);
        Serialisation.toJsonFile("formations_sans_themes_ni_metier.json",
                AlgoSuggestions.edgesKeys.edges().entrySet().stream()
                        .filter(e -> isFiliere(e.getKey()))
                        .filter(e -> e.getValue().stream().noneMatch(f -> isTheme(f) || isMetier(f)))
                        .map(e -> ServerData.getDebugLabel(e.getKey()))
                        .filter(e -> !e.contains("groupe"))
                        .toList(),
                true);


        /********************* JSON OUTPUT OF TAGS SOURCES **********************************/
        Map<String, Set<String>> tagsSourcesWithLabels = new HashMap<>();
        TagsSources tagsSources = TagsSources.load(ServerData.backPsupData.getCorrespondances());
        tagsSources.sources().forEach((s, strings) -> {
            Set<String> labels = strings.stream().map(ServerData::getLabel).collect(Collectors.toSet());
            tagsSourcesWithLabels.put(s, labels);
        });
        Serialisation.toJsonFile("tagsSourcesWithLabels.json", tagsSourcesWithLabels, true);

        /********************* JSON OUTPUT OF INNER DATA OF STATISTIQUES **********************************/
        Serialisation.toJsonFile("nomsfilieresFromStatistiques.json", statistiques.nomsFilieres, true);

        Serialisation.toJsonFile("frontAllLabels.json",statistiques.labels, true);

        Serialisation.toJsonFile("frontFormationsLabels.json",
                statistiques.labels.entrySet()
                        .stream().filter(e -> isFiliere(e.getKey()))
                        .filter(e -> !flGroups.keySet().contains(e.getKey()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ))
                , true);

        Serialisation.toJsonFile("frontMetiersLabels.json",
                statistiques.labels.entrySet()
                        .stream().filter(e -> isMetier(e.getKey()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ))
                , true);

        /********************* liste des LAS **********************************/
        Map<String, Pair<String, Integer>> las = new TreeMap<>();


        statistiques.getFilieres().forEach(filiere -> {
            if (filiere.isLas) {
                int flcod = filiere.cleFiliere;
                int capacite = getFormationsFromFil(Constants.gFlCodToFrontId(flcod))
                        .stream().filter(f -> backPsupData.las().contains(f.gTaCod))
                        .mapToInt(f -> f.capacite)
                        .sum();
                String key = Constants.gFlCodToFrontId(flcod);
                las.put(key, Pair.of(getLabel(key, key), capacite));
            }
        });
        Serialisation.toJsonFile("las.json", las, true);

        /********************* JSON OUTPUT OF TAXONOMIE **********************************/
        //on crée la taxonomie
        Map<String, Theme> indexTheme = new HashMap<>();
        ServerData.onisepData.thematiques().thematiques().forEach((s, s2) -> indexTheme.computeIfAbsent(s, z -> new Theme(s, s2)));
        Set<String> roots = new HashSet<>(indexTheme.keySet());
        ServerData.onisepData.thematiques().parent().forEach((s, s2) -> {
            Theme t = indexTheme.get(Constants.cleanup(s));
            roots.remove(Constants.cleanup(s));
            Theme t2 = indexTheme.get(Constants.cleanup(s2));
            if (t2 != null) {
                t2.sons.add(t);
            }
        });
        List<Theme> forest = indexTheme.entrySet().stream().filter(e -> roots.contains(e.getKey())).map(Map.Entry::getValue).toList();
        List<Theme> orphans = forest.stream().filter(n -> n.sons.isEmpty()).toList();
        Map<String, Long> racines = forest.stream()
                .filter(n -> !n.sons.isEmpty())
                .collect(Collectors.toMap(
                        n -> n.intitule,
                        Theme::size
                ));
        Serialisation.toJsonFile("taxonomie.json", forest, true);
        Serialisation.toJsonFile("orphans.json", orphans, true);
        Serialisation.toJsonFile("racines.json", racines, true);

        /********************* JSON OUTPUT OF URLS **********************************/
        PsupToOnisepLines billyLines = Serialisation.fromJsonFile(DataSources.getSourceDataFilePath(
                DataSources.ONISEP_PSUP_TO_IDEO_PATH),
                PsupToOnisepLines.class
        );
        Map<String, List<String>> lines =
                billyLines
                        .psupToIdeo2().stream()
                        .collect(Collectors.toMap(
                                PsupToOnisepLines.PsupToOnisepLine::G_FL_COD,
                                e -> Arrays.stream(e.IDS_IDEO2().split((";"))).map(String::trim).toList()
                        ));

        List<OnisepUrl> urls =
                lines.entrySet().stream()
                        .map(e -> new OnisepUrl(
                                Constants.FILIERE_PREFIX + e.getKey(),
                                ServerData.getLabel(Constants.FILIERE_PREFIX + e.getKey()),
                                ServerData.statistiques.liensOnisep.get(Constants.FILIERE_PREFIX + e.getKey()),
                                e.getValue()
                        ))
                        .sorted(Comparator.comparingInt(o -> Integer.parseInt(o.flCod.substring(2))))
                        .toList();


        Serialisation.toJsonFile("flcod_to_ideos_et_urls_onisep.json", urls, true);


        /**** stats fichier xml **/
        Formations formations = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.ONISEP_FORMATIONS_PATH),
                Formations.class
        );
        Set<String> knownFromXML = formations.formations().stream().map(fr.gouv.monprojetsup.data.update.onisep.formations.Formation::identifiant).collect(Collectors.toSet());
        Set<String> knownFromIdeoHoline = lines.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        knownFromIdeoHoline.retainAll(knownFromXML);

        FilieresToFormationsOnisep filieres = ServerData.onisepData.filieresToFormationsOnisep();
        PsupStatistiques data = ServerData.statistiques;

        Serialisation.toJsonFile("formationsOnisepDansLaCorr.json", filieres, true);
        Set<String> connues = filieres.filieres().stream().map(FilieresToFormationsOnisep.FiliereToFormationsOnisep::gFlCod).collect(Collectors.toSet());
        data.nomsFilieres.forEach((key, value) -> {
            if (!connues.contains(key)) {
                filieres.filieres().add(new FilieresToFormationsOnisep.FiliereToFormationsOnisep(
                        key,
                        "",
                        "",
                        value,
                        new ArrayList<>()
                ));
            }
        });

        ArrayList<FilieresToFormationsOnisep.FiliereToFormationsOnisep> copy = new ArrayList<>(filieres.filieres());
        copy.removeIf(
                f -> f.formationOniseps() != null
                        && f.formationOniseps().size() == 1
                        && f.formationOniseps().get(0).descriptifFormatCourt() != null
                        && !f.formationOniseps().get(0).descriptifFormatCourt().isEmpty()
        );

        Serialisation.toJsonFile("formations_sans_descriptif_details.json", copy, true);

        ArrayList<String> copy3 = new ArrayList<>();
        backPsupData.filActives().forEach(flCod -> {
            String key = Constants.gFlCodToFrontId(flCod);
            if (!desc.keyToDescriptifs().containsKey(key)) {
                String label = ServerData.getDebugLabel(key);
                if(!label.contains("null")) {
                    copy3.add(label);
                }
            }
        });
        Serialisation.toJsonFile("formations_sans_descriptif.json", copy3, true);

        ArrayList<String> copy4 = new ArrayList<>();
        edgesKeys.nodes().forEach(key -> {
            if (isMetier(key) && !desc.keyToDescriptifs().containsKey(key)) {
                String label = ServerData.getDebugLabel(key);
                if(!label.contains("null")) {
                    copy4.add(label);
                }
            }
        });
        Serialisation.toJsonFile("metiers_sans_descriptif.json", copy4, true);


        ArrayList<FilieresToFormationsOnisep.FiliereToFormationsOnisep> copy2 = new ArrayList<>(filieres.filieres());
        copy2.removeIf(

                f -> f.gFlLib().contains("apprentissage") || f.gFlLib().contains("LAS") || f.formationOniseps() != null
                        && !f.formationOniseps().isEmpty()
        );
        Serialisation.toJsonFile("formationsSansCorrIdeo.json", copy2, true);



        /**** correspondances filieres vers Metiers ****/
        Map<String, Set<String>> corrFoilMet = onisepData.getExtendedMetiersVersFormations();
        Map<String, List<String>> liens = corrFoilMet.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(keym -> ServerData.getLabel(Constants.cleanup(keym)))
                                .filter(Objects::nonNull)
                                .toList()
                ));
        liens.values().removeIf(List::isEmpty);

        Map<String, List<String>> filToMet = new HashMap<>();
        liens.forEach((key, metiers) -> {
            String formation = ServerData.getLabel(key);
            if (formation != null) {
                filToMet.put(formation + " (" + key + ")", metiers);
            }
        });
        Serialisation.toJsonFile("formationsVersMetiersOnisep.json", filToMet, true);

        /* chargement et groupement par clé */
        LienFormationMetier2 liste = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.METIERS_FORMATIONS_PAIRES_PATH_MANUEL),
                LienFormationMetier2.class
        );
        Map<String, Set<String> > filToMet2 = liste.paires().stream()
             .collect(Collectors.groupingBy(LienFormationMetier2.FormationMetierPaireOnisep::filiere))
                .entrySet()
                .stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue()
                                .stream()
                                .flatMap(f -> f.metiersList(onisepData.fichesMetiers()).stream())
                                .collect(Collectors.toSet()
                                )
                ));
        /* suppression des métiers déjà récupérés via onisep */
        filToMet2.forEach((s, strings) -> strings.removeAll(corrFoilMet.getOrDefault(s, Collections.emptySet())));
        Map<String, List<String> > filToMet3 =
                filToMet2.entrySet().stream()
                                .collect(Collectors.toMap(
                                        e -> ServerData.getLabel(e.getKey()) + " (" + e.getKey() + ")",
                                        e -> e.getValue().stream().map(f -> ServerData.getLabel(Constants.cleanup(f)) + " (" + f + ")").toList()
                                ));
        Serialisation.toJsonFile("formationsVersMetiersAjoutManuel.json", filToMet3, true);





        Set<String> keys = new HashSet<>();
        keys.addAll(backPsupData.displayedFilieres());
        keys.addAll(onisepData.metiers().metiers().keySet());

        List<String> itemsWithNoDescriptif = keys.stream().filter(key ->
                        !desc.keyToDescriptifs().containsKey(key)
                                && flGroups.getOrDefault(key, key).equals(key)
                                && !key.startsWith(Constants.CENTRE_INTERETS_ROME)
                                && !key.startsWith(Constants.CENTRE_INTERETS_ONISEP)
                                && !key.startsWith(Constants.THEME_PREFIX)
                )
                .map(
                        key -> ServerData.getLabel(key, key)
                ).sorted().toList();
        Serialisation.toJsonFile("itemsWithNoDescriptifs.json", itemsWithNoDescriptif, true);

        Serialisation.toJsonFile("descriptifs.json", desc, true);




    }

    private static void exportCentresDinterets() throws IOException {
        Interets interets = onisepData.interets();
        try(CsvTools csv = new CsvTools("centresInterets.csv",',')) {
            csv.append(List.of("id", "label"));
            interets.interets().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> {
                        try {
                            csv.append(e.getKey());
                            csv.append(e.getValue());
                            csv.newLine();
                        } catch (IOException f) {
                            throw new RuntimeException(f);
                        }
                    });
        }
    }

    private static void exportLiensDomainesMetiers() throws IOException {
       Map<String, List<String>> liensDomainesNomsMetiers
                = liensDomainesMetiers.entrySet().stream()
                .flatMap(e -> e.getValue().stream().map(f -> Pair.of(e.getKey(), f)))
                .collect(Collectors.groupingBy(p -> p.getLeft().domaine_onisep()))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                                e -> e.getValue().stream().map(p -> getDebugLabel(p.getRight())).toList())
                )
                ;
        Serialisation.toJsonFile("liensDomainesNomsMetiers.json", liensDomainesNomsMetiers, true);

        Map<String, List<String>> liensSousDomainesNomsMetiers
                = liensDomainesMetiers.entrySet().stream()
                .flatMap(e -> e.getValue().stream().map(f -> Pair.of(e.getKey(), f)))
                .collect(Collectors.groupingBy(p -> p.getLeft().sous_domaine_onisep()))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().map(p -> getDebugLabel(p.getRight())).toList())
                )
                ;
        Serialisation.toJsonFile("liensSousDomainesNomsMetiers.json", liensSousDomainesNomsMetiers, true);


    }

    private static void comparerMoyennesBacsEtSco() throws IOException {
        /** comparaison moyennes **/
        Map<String, Pair<Triple<Double, Double, Double>, Triple<Double, Double, Double>>> moyennes = ServerData.getStatsMoyGenVsBac();
        Serialisation.toJsonFile("compMoyennes.json",
                moyennes,
                true
        );
        try(CsvTools moyennesCsvWriter = new CsvTools("moyennes.csv",',')) {
            moyennesCsvWriter.append(List.of("filiere", "medianeMoyGen", "medianeMoyBac", "per25MoyGen", "per25MoyBac", "per75MoyGen", "per75MoyBac"));
            for (Map.Entry<String, Pair<Triple<Double, Double, Double>, Triple<Double, Double, Double>>> entry : moyennes.entrySet()) {
                String k = entry.getKey();
                Pair<Triple<Double, Double, Double>, Triple<Double, Double, Double>> v = entry.getValue();
                Triple<Double, Double, Double> tmoygen = v.getLeft();
                Triple<Double, Double, Double> tbac = v.getRight();
                if (v != null && v.getLeft() != null && v.getRight() != null) {
                    moyennesCsvWriter.append(k.replace(",", " "));
                    moyennesCsvWriter.append(tmoygen.getMiddle());
                    moyennesCsvWriter.append(tbac.getMiddle());
                    moyennesCsvWriter.append(tmoygen.getLeft());
                    moyennesCsvWriter.append(tbac.getLeft());
                    moyennesCsvWriter.append(tmoygen.getRight());
                    moyennesCsvWriter.append(tbac.getRight());
                    moyennesCsvWriter.newLine();
                }
            }
        }

    }

    private static Map<String, Pair<Triple<Double, Double, Double>, Triple<Double, Double, Double>>> getStatsMoyGenVsBac() {
        Set<String> displayedFilieres = ServerData.backPsupData.displayedFilieres();
        return displayedFilieres
                        .stream()
                        .distinct()
                        .filter(key -> ServerData.getLabel(key) != null)
                        .collect(Collectors.toMap(
                                ServerData::getLabel,
                                ServerData::getPaireMoyenne

                        ));
    }

    public static List<String> getFormationsOfInterest(@Nullable List<String> keys, @Nullable Set<String> cities) {
        if(keys == null || cities == null || keys.isEmpty() || cities.isEmpty()) return Collections.emptyList();
        return cities.stream().flatMap(city ->
                        keys.stream()
                                .flatMap(key -> reverseFlGroups.containsKey(key)
                                        ? getDistanceKm(reverseFlGroups.get(key), city).stream()
                                        : getDistanceKm(key, city).stream()
                                )
                ).filter(Objects::nonNull)
                .map(Explanation.ExplanationGeo::form)
                .toList();
    }

    public static EDSAggAnalysis getEDS(PsupData backPsupData, PsupStatistiques statistiques, Specialites specialites, boolean specifiques, boolean prettyPrint) {

        EDSAggAnalysis analyses = new EDSAggAnalysis();

        backPsupData.filActives().forEach(gFlCod -> {
            String key = Constants.gFlCodToFrontId(gFlCod);
            String gFlLib = statistiques.nomsFilieres.get(key);
            if(gFlLib != null) {
                String ppkey = prettyPrint ? ServerData.getDebugLabel(key) : key;
                //les nbAdmisEDS
                EDSAnalysis analysis = analyses.analyses().computeIfAbsent(ppkey, z -> new EDSAnalysis(
                        gFlCod,
                        gFlLib,
                        backPsupData.getAttendus(gFlCod),
                        backPsupData.getRecoPremGeneriques(gFlCod),
                        backPsupData.getRecoTermGeneriques(gFlCod)
                ));
                Map<Integer, Integer> statsEds = statistiques.getStatsSpec(key);
                if(statsEds != null) {
                    statsEds.forEach((iMtCod, pct) -> {
                        String name = specialites.specialites().get(iMtCod);
                        if(name != null) {
                            analysis.nbAdmisEDS().put(pct, name);
                        }
                    });
                }
                Map<Integer, Integer> statsEds2 = statistiques.getStatsSpecCandidats(key);
                if(statsEds2 != null) {
                    statsEds2.forEach((iMtCod, pct) -> {
                        String name = specialites.specialites().get(iMtCod);
                        if(name != null) {
                            analysis.nbCandidatsEDS().put(pct, name);
                        }
                    });
                }

                if(specifiques) {
                    //les messages
                    //on aggrege tous les codes jurys de la filiere
                    List<Integer> gTaCods = getFormationsFromFil(key).stream().map(f -> f.gTaCod).toList();
                    Set<String> juryCodes = backPsupData.getJuryCodesFromGTaCods(gTaCods);
                    analysis.recosScoPremSpecifiques().putAll(backPsupData.getRecoScoPremiere(juryCodes));
                    analysis.recosScoTermSpecifiques().putAll(backPsupData.getRecoScoTerminale(juryCodes));
                }
            }
        });
        return analyses;
    }

    public static Map<String, Attendus> getEDSSimple(PsupData psupData, PsupStatistiques data, Specialites specs, boolean specifiques) {
        EDSAggAnalysis eds = getEDS(psupData, data, specs, specifiques, false);
        return eds.getFrontData();
    }




    record OnisepUrl(
            String flCod,
            String libelle,
            String url,
            List<String> codesIdeos)
    {

    }

    static final class Theme {
        public final String cle;
        public final String intitule;
        public final List<Theme> sons = new ArrayList<>();

        public long size() {
            return 1 + sons.stream().mapToLong(Theme::size).sum();
        }
        Theme(
                String cle,
                String intitule
        ) {
            this.cle = cle;
            this.intitule = intitule;
        }
    }

    public static String getLabel(String key) {
        return ServerData.statistiques.labels.getOrDefault(
                key,
                ServerData.statistiques.nomsFilieres.get(key)
        );
    }

    public static String getDebugLabel(String key) {
        return getLabel(key) + " (" + key  + ")";
    }

    public static String getLabel(String key, String defaultValue) {
        return ServerData.statistiques.labels.getOrDefault(key, defaultValue);
    }

    private static final LevenshteinDistance levenAlgo = new LevenshteinDistance(10);
    public static @Nullable String getFlCodFromLabel(String expectation) {
        return
                statistiques.labels.entrySet().stream()
                        .map(e -> Pair.of(e.getKey(), levenAlgo.apply(e.getValue(), expectation)))
                        .filter(e -> e.getRight() >= 0)
                        .min(Comparator.comparingInt(Pair::getRight))
                        .map(Pair::getLeft)
                        .orElse(null);
    }

}

