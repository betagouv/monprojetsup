package fr.gouv.monprojetsup.data.analysis;


import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.Helpers;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.config.DataServerConfig;
import fr.gouv.monprojetsup.data.model.attendus.Attendus;
import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.data.model.formations.ActionFormationOni;
import fr.gouv.monprojetsup.data.model.formations.Filiere;
import fr.gouv.monprojetsup.data.model.formations.FilieresToFormationsOnisep;
import fr.gouv.monprojetsup.data.model.interets.Interets;
import fr.gouv.monprojetsup.data.model.metiers.Metiers;
import fr.gouv.monprojetsup.data.model.specialites.SpecialitesLoader;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.model.stats.Statistique;
import fr.gouv.monprojetsup.data.model.tags.TagsSources;
import fr.gouv.monprojetsup.data.model.thematiques.Thematiques;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.tools.csv.CsvTools;
import fr.gouv.monprojetsup.data.update.UpdateFrontData;
import fr.gouv.monprojetsup.data.update.onisep.LienFormationMetier2;
import fr.gouv.monprojetsup.data.update.onisep.billy.PsupToOnisepLines;
import fr.gouv.monprojetsup.data.update.onisep.formations.Formations;
import fr.gouv.monprojetsup.data.update.psup.PsupData;
import fr.gouv.parcoursup.carte.modele.modele.Etablissement;
import fr.gouv.parcoursup.carte.modele.modele.Formation;
import fr.gouv.parcoursup.carte.modele.modele.JsonCarte;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.FILIERE_PREFIX;
import static fr.gouv.monprojetsup.data.ServerData.*;
import static fr.gouv.monprojetsup.data.tools.Serialisation.fromZippedJson;

/**
 * Classe principale pour l'analyse de la qualité des données du serveur.
 */
@Slf4j
public class ServerDataAnalysis {

    public static void main(String[] args) throws Exception {

        //fixme WebServer.loadConfig();

        DataServerConfig.load();

        compareActionsFormations();

        ServerData.load();

        DataServerConfig.load();

        /*
        log.info("Chargement et minimization de " + DataSources.FRONT_MID_SRC_PATH);
        PsupStatistiques data = fromZippedJson(DataSources.getSourceDataFilePath(DataSources.FRONT_MID_SRC_PATH), PsupStatistiques.class);
        data.removeFormations();
        data.removeSmallPopulations();

        log.info("Chargement et nettoyage de " + DataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME));
        PsupData psupData = Serialisation.fromZippedJson(DataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME), PsupData.class);
        psupData.cleanup();

        log.info("Chargement des données ROME");
        RomeData romeData = RomeData.load();

        log.info("Chargement des données Onisep");
        final OnisepData onisepData = OnisepData.fromFiles();

        log.info("Insertion des données ROME dans les données Onisep");
        onisepData.insertRomeData(romeData.centresInterest()); //before updateLabels

        log.info("Maj des données Onisep (noms des filières et urls)");
        data.updateLabels(onisepData, psupData, data.getLASCorrespondance().lasToGeneric());
        */

        exportCentresDinteretsEtThemes();

        outputResumesDescriptifsFormationsEtMetiersExtraits();

        exportDeltaWithBillyCorr();

        exportLiensDomainesMetiers();


        exportAttendus();

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


        outputMetiersSansDescriptifs(desc);

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
                        .stream().filter(e -> Helpers.isFiliere(e.getKey()))
                        .filter(e -> !flGroups.containsKey(e.getKey()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ))
                , true);

        Serialisation.toJsonFile("frontMetiersLabels.json",
                statistiques.labels.entrySet()
                        .stream().filter(e -> Helpers.isMetier(e.getKey()))
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

    private static void exportDeltaWithBillyCorr() {
        @NotNull Map<String, Set<String>> copy =  onisepData.edgesMetiersFilieres().edges();
        //we keep only formation
        copy.keySet().removeIf(s -> !Helpers.isFiliere(s));

        onisepData.billy().psupToIdeo2().forEach(
                line -> {
                    String gFlCod = line.G_FL_COD();
                    String idsIdeo2 = line.IDS_IDEO2();
                    Set<String> ids = Arrays.stream(idsIdeo2.split(";")).map(String::trim).collect(Collectors.toSet());
                    boolean changed = copy.computeIfAbsent("fl" + gFlCod, k -> new TreeSet<>()).removeAll(ids);
                    if(changed) {

                    }
                    //to be continued
                }
        );
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
            csv.appendHeaders(
                    List.of(
                            "CODEFORMATION (gFrCod)",
                            "LIBELLÉFORMATION (gFrLib)",
                            "CODESPÉCIALITÉ (gFlCod)",
                            "LIBELLÉTYPEFORMATION (gFlLib)",
                            "LIS_ID_ONI2",
                            "UAI ETABLISSEMENT",
                            "NOM ETABLISSEMENT",
                            "URL ETABLISSEMENT",
                            "lat/lng ETABLISSEMENT",
                            "NOM FORMATION"
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

                for (List<Formation> forms : psupUAI.values()) {

                    Set<String> uaiAlreadySeen = new HashSet<>();
                    for (Formation f : forms) {
                        csv.append(line.G_FR_COD());
                        csv.append(line.G_FR_LIB());
                        csv.append(gFlCod);
                        csv.append(line.G_FL_LIB());
                        csv.append(line.IDS_IDEO2());
                        String uai = f.getGea();
                        Etablissement et = carte.etablissements.get(uai);
                        csv.append(uai);
                        csv.append(et.getNm());
                        csv.append(et.getUrl());
                        csv.append(Arrays.stream(et.getPos()).map(String::valueOf).collect(Collectors.joining(",")));
                        csv.append(f.getNm());
                        csv.newLine();
                    }
                }
            }
        }


    }

    private static String libelle(List<fr.gouv.parcoursup.carte.modele.modele.Formation> f, JsonCarte carte) {
        if(f.isEmpty()) return "";
        return libelle(f.get(0), carte);
    }

    private static String libelle(fr.gouv.parcoursup.carte.modele.modele.Formation f, JsonCarte carte) {
        if(f.getGealib() != null) return f.getGealib();
        return carte.etablissements.get(f.getGea()).getUrl();
    }
    private static void exportCentresDinteretsEtThemes() throws IOException {
        Interets interets = onisepData.interets();
        try(CsvTools csv = new CsvTools("centresInterets.csv",',')) {
            csv.appendHeaders(List.of("id", "label"));
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
        Thematiques thematiques = onisepData.thematiques();
        try(CsvTools csv = new CsvTools("thematiques.csv",',')) {
            csv.appendHeaders(List.of("id", "label"));
            thematiques.thematiques().entrySet().stream()
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

    private static void exportAttendus() throws IOException {
        val attendus = getAttendusParGroupes();
        try(CsvTools csv = new CsvTools("attendus.csv",',')) {
            csv.appendHeaders(List.of("type", "énonce","formations"));
            attendus.forEach((stringStringPair, strings) -> {
                try {
                    csv.append(stringStringPair.getLeft());
                    csv.append(stringStringPair.getRight());
                    csv.append(strings.stream().collect(Collectors.joining("\n")));
                    csv.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
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
            moyennesCsvWriter.appendHeaders(List.of("filiere", "medianeMoyGen", "medianeMoyBac", "per25MoyGen", "per25MoyBac", "per75MoyGen", "per75MoyBac"));
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

    private static void outputMetiersSansDescriptifs(Descriptifs desc) throws IOException {
        ArrayList<String> copy4 = new ArrayList<>();
        onisepData.metiers().metiers().keySet().forEach(key -> {
            if (Helpers.isMetier(key) && !desc.keyToDescriptifs().containsKey(key)) {
                String label = ServerData.getDebugLabel(key);
                if(!label.contains("null")) {
                    copy4.add(label);
                }
            }
        });
        Serialisation.toJsonFile("metiers_sans_descriptif.json", copy4, true);

    }

    public static void outputResumesDescriptifsFormationsEtMetiersExtraits() throws IOException {

        log.info("Chargement et minimization de " + DataSources.FRONT_MID_SRC_PATH);
        PsupStatistiques data = fromZippedJson(DataSources.getSourceDataFilePath(DataSources.FRONT_MID_SRC_PATH), PsupStatistiques.class);
        data.removeFormations();
        data.removeSmallPopulations();

        log.info("Chargement et nettoyage de " + DataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME));
        PsupData psupData = Serialisation.fromZippedJson(DataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME), PsupData.class);
        psupData.cleanup();

        log.info("Ajout des liens metiers");
        Map<String, String> urls = new HashMap<>(data.liensOnisep);
        onisepData.metiers().metiers().forEach((s, metier) -> {
            //onisep.fr/http/redirections/metier/slug/[identifiant]
        });

        Map<String, Attendus> eds = Attendus.getAttendus(
                psupData,
                data,
                SpecialitesLoader.load(ServerData.statistiques),
                false
        );

        UpdateFrontData.DataContainer data2 = UpdateFrontData.DataContainer.load(
                psupData,
                onisepData,
                urls,
                data.getLASCorrespondance(),
                eds);

        try (CsvTools csv = new CsvTools("resumesDescriptifsFormations.csv", ',')) {
            csv.appendHeaders(List.of("code filière ou groupe (glcod)", "intitulé web", "code type formation", "intitule type formation",
                    "url onisep",
                    "url psup",
                    "resume",
                    "attendus",
                    "recommandations sur le parcours au lycée"));

            for (String flStr : filieresFront) {
                if (statistiques.getLASCorrespondance().isLas(flStr)) {
                    continue;
                }
                String label2 = getLabel(flStr, flStr);
                if (label2.contains("apprentissage") || label2.contains("LAS")) {
                    continue;
                }

                csv.append(flStr);
                csv.append(label2);

                int code = Integer.parseInt(flStr.substring(2));
                if (flStr.startsWith("fr")) {
                    csv.append(flStr);
                    String typeMacro = backPsupData.formations().typesMacros.getOrDefault(code, "");
                    csv.append(typeMacro);
                } else {
                    Filiere fil = backPsupData.formations().filieres.get(code);
                    if (fil == null) {
                        throw new RuntimeException("no data on filiere " + flStr);
                    }
                    csv.append("fr" + fil.gFrCod);
                    String typeMacro = backPsupData.formations().typesMacros.getOrDefault(fil.gFrCod, "");
                    csv.append(typeMacro);
                }

                val listeFilieres = reverseFlGroups.getOrDefault(flStr, Set.of(flStr));

                //liens onisep (distaincts)
                csv.append(
                        listeFilieres
                                .stream().map(s -> data.liensOnisep.getOrDefault(s, ""))
                                .filter(s -> !s.isEmpty())
                                .distinct()
                                .collect(Collectors.joining("\n"))
                );

                csv.append("https://dossier.parcoursup.fr/Candidat/carte?search=" + listeFilieres
                        .stream()
                                .distinct()
                        .map(fl -> fl + "x").collect(Collectors.joining("%20")));

                Descriptifs.Descriptif descriptif = data2.descriptifs().keyToDescriptifs().get(flStr);

                if (descriptif != null) {
                    csv.append(descriptif.getFrontRendering());
                } else {
                    csv.append("");
                }
                csv.append(listeFilieres.stream()
                        .map(fl -> Pair.of(fl, eds.get(fl)))
                        .filter(p -> p.getRight() != null && p.getRight().attendus() != null)
                        .map(p -> getLabel(p.getLeft()) + "\n" + p.getRight().attendus())
                        .filter(o ->  o!= null && !o.isBlank()).distinct()
                        .collect(Collectors.joining("\n\n****    cas multiple    ****\n\n")));
                csv.append(listeFilieres.stream()
                        .map(fl -> Pair.of(fl, eds.get(fl)))
                        .filter(p -> p.getRight() != null && p.getRight().recoEDS() != null)
                        .map(p -> getLabel(p.getLeft()) + "\n" + p.getRight().recoEDS())
                        .filter(o ->  o!= null && !o.isBlank()).distinct()
                        .collect(Collectors.joining("\n\n****    cas multiple     ****\n\n")));
                csv.newLine();
            }
        }

        try (CsvTools csv = new CsvTools("metiersExtraitsDesDescriptifsformations.csv", ',')) {
            csv.appendHeaders(List.of("code filière ou groupe (glcod)", "intitulé web", "code type formation", "intitule type formation",
                    "métiers extraits des descriptifs (codes)",
                    "intitulé onisep",
                    "extrait du descriptif",
                    "descriptif",
                    "url psup",
                    "url onisep"
                    ));

            for (String flStr : filieresFront) {
                if (statistiques.getLASCorrespondance().isLas(flStr)) {
                    continue;
                }
                String label2 = getLabel(flStr, flStr);
                if (label2.contains("apprentissage") || label2.contains("LAS")) {
                    continue;
                }

                Descriptifs.Descriptif descriptif = data2.descriptifs().keyToDescriptifs().get(flStr);
                if(descriptif == null || descriptif.hasError() || descriptif.presentation() == null) continue;

                    int i = descriptif.presentation().indexOf("Exemples de métiers");
                    if(i < 0) continue;
                List<Triple<String, String, Metiers.Metier>> triplets = new ArrayList<>(onisepData.metiers().extractMetiers(descriptif.presentation().substring(i)));
                if(triplets.isEmpty()) continue;


                csv.append(flStr);
                csv.append(label2);

                int code = Integer.parseInt(flStr.substring(2));
                if (flStr.startsWith("fr")) {
                    csv.append(flStr);
                    String typeMacro = backPsupData.formations().typesMacros.getOrDefault(code, "");
                    csv.append(typeMacro);
                } else {
                    Filiere fil = backPsupData.formations().filieres.get(code);
                    if (fil == null) {
                        throw new RuntimeException("no data on filiere " + flStr);
                    }
                    csv.append("fr" + fil.gFrCod);
                    String typeMacro = backPsupData.formations().typesMacros.getOrDefault(fil.gFrCod, "");
                    csv.append(typeMacro);
                }

                val listeFilieres = reverseFlGroups.getOrDefault(flStr, Set.of(flStr));


                csv.append(triplets.stream().map(Triple::getLeft).collect(Collectors.joining("\n")));
                csv.append(triplets.stream().map(t -> t.getRight().lib()).collect(Collectors.joining("\n")));
                csv.append(triplets.stream().map(Triple::getMiddle).collect(Collectors.joining("\n")));



                csv.append(descriptif.getFrontRendering());

                csv.append("https://dossier.parcoursup.fr/Candidat/carte?search=" + listeFilieres
                        .stream()
                        .distinct()
                        .map(fl -> fl + "x").collect(Collectors.joining("%20")));

                //liens onisep (distaincts)
                csv.append(
                        listeFilieres
                                .stream().map(s -> data.liensOnisep.getOrDefault(s, ""))
                                .filter(s -> !s.isEmpty())
                                .distinct()
                                .collect(Collectors.joining("\n"))
                );

                csv.newLine();
            }
        }

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
