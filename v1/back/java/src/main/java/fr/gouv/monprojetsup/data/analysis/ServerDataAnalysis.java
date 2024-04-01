package fr.gouv.monprojetsup.data.analysis;


import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.data.model.eds.Attendus;
import fr.gouv.monprojetsup.data.model.formations.ActionFormationOni;
import fr.gouv.monprojetsup.data.model.formations.Filiere;
import fr.gouv.monprojetsup.data.model.formations.FilieresToFormationsOnisep;
import fr.gouv.monprojetsup.data.model.interets.Interets;
import fr.gouv.monprojetsup.data.model.specialites.SpecialitesLoader;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.model.stats.Statistique;
import fr.gouv.monprojetsup.data.model.tags.TagsSources;
import fr.gouv.monprojetsup.data.update.UpdateFrontData;
import fr.gouv.monprojetsup.data.update.onisep.LienFormationMetier2;
import fr.gouv.monprojetsup.data.update.onisep.OnisepData;
import fr.gouv.monprojetsup.data.update.onisep.billy.PsupToOnisepLines;
import fr.gouv.monprojetsup.data.update.onisep.formations.Formations;
import fr.gouv.monprojetsup.data.update.psup.PsupData;
import fr.gouv.monprojetsup.data.update.rome.RomeData;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.tools.Serialisation;
import fr.gouv.monprojetsup.tools.csv.CsvTools;
import fr.gouv.monprojetsup.web.server.WebServer;
import fr.gouv.parcoursup.carte.modele.modele.JsonCarte;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.*;
import static fr.gouv.monprojetsup.data.ServerData.*;
import static fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions.*;
import static fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions.LOGGER;
import static fr.gouv.monprojetsup.tools.Serialisation.fromZippedJson;
import static fr.parcoursup.carte.algos.Filiere.LAS_CONSTANT;

/**
 * Classe principale pour l'analyse de la qualité des données du serveur.
 */
public class ServerDataAnalysis {

    public static void main(String[] args) throws Exception {

        WebServer.loadConfig();

        compareActionsFormations();

        ServerData.load();

        AlgoSuggestions.initialize();

        exportFrontDatasToCsvForDescriptifsImprovements();

        Serialisation.toJsonFile("relatedToHealth.json",
                AlgoSuggestions.getRelatedToHealth()
                        .stream().map(ServerData::getDebugLabel)
                        .toList()
                , true);

        exportLiensDomainesMetiers();

        exportCentresDinterets();

        Serialisation.toJsonFile("thematiques.json", ServerData.onisepData.thematiques().thematiques(), true);

        /* *** items sans descriptis  ****/
        Descriptifs desc = UpdateFrontData.DataContainer.loadDescriptifs(ServerData.onisepData, flGroups, statistiques.getLASCorrespondance().lasToGeneric());
        Set<Integer> lasses = backPsupData.las();
        List<String> filieresAffichees = backPsupData.filActives().stream()
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
            if (statistiques.nomsFilieres.containsKey(s2)) {
                set.add(l2);
            }
        });
        Serialisation.toJsonFile("regroupements.json", regroupements, true);

        AlgoSuggestions.createGraph();
        edgesLabels.createLabelledGraphFrom(edgesKeys, statistiques.labels);

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
                        PsupToOnisepLines.PsupToOnisepLine::IDS_IDEO2
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
                .filter(e -> e.getValue().stream().noneMatch(ServerData::isFiliere))
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
                        .filter(e -> e.getValue().stream().noneMatch(ServerData::isTheme))
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


        /* ******************** JSON OUTPUT OF TAGS SOURCES **********************************/
        Map<String, Set<String>> tagsSourcesWithLabels = new HashMap<>();
        TagsSources tagsSources = TagsSources.load(backPsupData.getCorrespondances());
        tagsSources.sources().forEach((s, strings) -> {
            Set<String> labels = strings.stream().map(ServerData::getLabel).collect(Collectors.toSet());
            tagsSourcesWithLabels.put(s, labels);
        });
        Serialisation.toJsonFile("tagsSourcesWithLabels.json", tagsSourcesWithLabels, true);

        /* ******************** JSON OUTPUT OF INNER DATA OF STATISTIQUES **********************************/
        Serialisation.toJsonFile("nomsfilieresFromStatistiques.json", statistiques.nomsFilieres, true);

        Serialisation.toJsonFile("frontAllLabels.json",statistiques.labels, true);

        Serialisation.toJsonFile("frontFormationsLabels.json",
                statistiques.labels.entrySet()
                        .stream().filter(e -> isFiliere(e.getKey()))
                        .filter(e -> !flGroups.containsKey(e.getKey()))
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

        /* ******************** liste des LAS **********************************/
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

        /* ******************** JSON OUTPUT OF TAXONOMIE **********************************/
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

        /* ******************** JSON OUTPUT OF URLS **********************************/
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
                                FILIERE_PREFIX + e.getKey(),
                                ServerData.getLabel(FILIERE_PREFIX + e.getKey()),
                                statistiques.liensOnisep.get(FILIERE_PREFIX + e.getKey()),
                                e.getValue()
                        ))
                        .sorted(Comparator.comparingInt(o -> Integer.parseInt(o.flCod.substring(2))))
                        .toList();


        Serialisation.toJsonFile("flcod_to_ideos_et_urls_onisep.json", urls, true);


        /* *** stats fichier xml **/
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
        PsupStatistiques data = statistiques;

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



        /* *** correspondances filieres vers Metiers ****/
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

    private static void exportFrontDatasToCsvForDescriptifsImprovements() throws IOException {

        WebServer.loadConfig();

        LOGGER.info("Chargement et minimization de " + DataSources.FRONT_MID_SRC_PATH);
        PsupStatistiques data = fromZippedJson(DataSources.getSourceDataFilePath(DataSources.FRONT_MID_SRC_PATH), PsupStatistiques.class);
        data.removeFormations();
        data.removeSmallPopulations();

        LOGGER.info("Chargement et nettoyage de " + DataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME));
        PsupData psupData = Serialisation.fromZippedJson(DataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME), PsupData.class);
        psupData.cleanup();

        LOGGER.info("Chargement des données ROME");
        RomeData romeData = RomeData.load();

        LOGGER.info("Chargement des données Onisep");
        final OnisepData onisepData = OnisepData.fromFiles();

        LOGGER.info("Insertion des données ROME dans les données Onisep");
        onisepData.insertRomeData(romeData.centresInterest()); //before updateLabels

        LOGGER.info("Maj des données Onisep (noms des filières et urls)");
        data.updateLabels(onisepData, psupData, data.getLASCorrespondance().lasToGeneric());

        LOGGER.info("Ajout des liens metiers");
        Map<String, String> urls = new HashMap<>(data.liensOnisep);
        onisepData.metiers().metiers().forEach((s, metier) -> {
            //onisep.fr/http/redirections/metier/slug/[identifiant]
        });

        Map<String, Attendus> eds = ServerData.getEDSSimple(
                psupData,
                data,
                SpecialitesLoader.load(),
                false
        );
        UpdateFrontData.DataContainer data2 = UpdateFrontData.DataContainer.load(psupData, onisepData, urls, data.getLASCorrespondance(), eds);



        try (CsvTools csv = new CsvTools("resumesDescriptifsFormations.csv", ',')) {
            csv.append(List.of("code filière (glcod)", "intitulé web", "code type formation", "intitule type formation",
                    "url onisep",
                    "url psup",
                    "resume"));
            List<Integer> keys =
                    edgesKeys.edges().keySet().stream().filter(s -> s.startsWith(FILIERE_PREFIX))
                            .mapToInt(s -> Integer.parseInt(s.substring(2)))
                            .sorted()
                            .boxed()
                            .toList();

            for (Integer flCod : keys) {
                if (!backPsupData.filActives().contains(flCod)) {
                    continue;
                }
                String flStr = gFlCodToFrontId(flCod);
                if(flCod >= LAS_CONSTANT) continue;
                String label2 = data.labels.getOrDefault(flStr, flStr);
                Filiere fil = backPsupData.formations().filieres.get(flCod);
                if (label2.contains("apprentissage") || label2.contains("LAS")) {
                    continue;
                }
                if (fil == null) {
                    throw new RuntimeException("no data on filiere " + flCod);
                }
                csv.append(flStr);
                csv.append(label2);
                csv.append(fil.gFrCod);
                String typeMacro = backPsupData.formations().typesMacros.getOrDefault(fil.gFrCod, "");
                csv.append(typeMacro);
                csv.append(data.liensOnisep.getOrDefault(flStr, ""));
                csv.append("https://dossier.parcoursup.fr/Candidat/carte?search=" + flStr + "x");
                Descriptifs.Descriptif descriptif = data2.descriptifs().keyToDescriptifs().get(flStr);
                if(descriptif != null) {
                    csv.append(descriptif.getFrontRendering());
                } else {
                    csv.append("");
                }
                csv.newLine();
            }
        }
    }

    public static final String ONISEP_ACTIONS_FORMATIONS_PATH = "ideo_actions_formations/ideo-actions_de_formation_initiale-univers_enseignement_superieur.json";
    public static final String CARTE_JSON_PATH = "psup_actions_formations/psupdata-06-03-2024-11-05-05.json";

    private static void compareActionsFormations() throws IOException {
        List<ActionFormationOni> actionsOni = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(ONISEP_ACTIONS_FORMATIONS_PATH),
                new TypeToken<List<ActionFormationOni>>() {
                }.getType()
        );

        JsonCarte carte = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(CARTE_JSON_PATH),
                JsonCarte.class
        );

        PsupToOnisepLines lines = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(
                        DataSources.ONISEP_PSUP_TO_IDEO_PATH
                ),
                PsupToOnisepLines.class
        );


        try (CsvTools csv = new CsvTools("comparatif_actions_formations_psup_oni.csv", ',')) {
            //on lit la correspondance de JM
            csv.append(
                    List.of(
                            "CODEFORMATION (gFrCod)",
                            "LIBELLÉFORMATION (gFrLib)",
                            "CODESPÉCIALITÉ (gFlCod)",
                            "LIBELLÉTYPEFORMATION (gFlLib)",
                            "LIS_ID_ONI2",
                            "dans_psup_mais_pas_dans_onisep"
                    )
            );

            for (PsupToOnisepLines.PsupToOnisepLine line : lines.psupToIdeo2()) {
                String gFlCodStr = line.G_FL_COD();
                int gFlCod = Integer.parseInt(gFlCodStr);
                Set<String> idsIdeo2 =
                        Arrays.stream(line.IDS_IDEO2().split(";"))
                                .map(String::trim).collect(Collectors.toSet());
                idsIdeo2.remove("");
                idsIdeo2.remove(null);
                if (idsIdeo2.isEmpty()) continue;
                //on calcule les uai des établissements de ce gFlCod, côté psup
                Map<String, List<fr.gouv.parcoursup.carte.modele.modele.Formation>> psupUAI = carte.formations.values().stream()
                        .filter(f -> f.getFl() == gFlCod)
                        .collect(
                                Collectors.groupingBy(
                                        fr.gouv.parcoursup.carte.modele.modele.Formation::getGea
                                )
                        );
                //on calcule les uai des établissements de ce gFlCod, côté onisep

                Map<String, List<ActionFormationOni>> onisepUai = actionsOni.stream()
                        .filter(f -> idsIdeo2.contains(f.getIdOnisep()))
                        .collect(Collectors.groupingBy(ActionFormationOni::ens_code_uai));

                psupUAI.keySet().removeAll(onisepUai.keySet());
                if (psupUAI.isEmpty()) continue;

                csv.append(line.G_FR_COD());
                csv.append(line.G_FR_LIB());
                csv.append(gFlCod);
                csv.append(line.G_FL_LIB());
                csv.append(line.IDS_IDEO2());
                csv.append(psupUAI.values().stream()
                        .map(f -> libelle(f, carte))
                        .collect(Collectors.joining("\n")));
                csv.newLine();
            }
        }


    }

    private static String libelle(List<fr.gouv.parcoursup.carte.modele.modele.Formation> f, JsonCarte carte) {
        if(f.isEmpty()) return "";
        return libelle(f.get(0), carte);
    }

    private static String libelle(fr.gouv.parcoursup.carte.modele.modele.Formation f, JsonCarte carte) {
        if(f.getGealib() != null) return f.getGealib();
        return carte.etablissements.get(f.getGea()).getUrl() + "   (UAI:" +f.getGea() + ")";
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
        /* * comparaison moyennes **/
        Map<String, Pair<Triple<Double, Double, Double>, Triple<Double, Double, Double>>> moyennes = getStatsMoyGenVsBac();
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
                if (v.getLeft() != null && v.getRight() != null) {
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
        Set<String> displayedFilieres = backPsupData.displayedFilieres();
        return displayedFilieres
                .stream()
                .distinct()
                .filter(key -> ServerData.getLabel(key) != null)
                .collect(Collectors.toMap(
                        ServerData::getLabel,
                        ServerDataAnalysis::getPaireMoyenne

                ));
    }

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

    record OnisepUrl(
            String flCod,
            String libelle,
            String url,
            List<String> codesIdeos)
    {

    }
}
