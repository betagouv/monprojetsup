package fr.gouv.monprojetsup.data.audit;


import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.infrastructure.model.descriptifs.DescriptifsFormationsMetiers;
import fr.gouv.monprojetsup.suggestions.domain.Constants;
import fr.gouv.monprojetsup.suggestions.domain.Helpers;
import fr.gouv.monprojetsup.suggestions.infrastructure.DataSources;
import fr.gouv.monprojetsup.suggestions.infrastructure.carte.Etablissement;
import fr.gouv.monprojetsup.suggestions.infrastructure.carte.FormationCarte;
import fr.gouv.monprojetsup.suggestions.infrastructure.carte.JsonCarte;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.attendus.Attendus;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.attendus.AttendusDetailles;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.descriptifs.DescriptifsFormations;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.formations.ActionFormationOni;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.formations.Filiere;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.formations.FilieresToFormationsOnisep;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.interets.Interets;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.metiers.Metiers;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.stats.Statistique;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.tags.TagsSources;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.thematiques.Thematiques;
import fr.gouv.monprojetsup.suggestions.infrastructure.onisep.billy.PsupToOnisepLines;
import fr.gouv.monprojetsup.suggestions.infrastructure.onisep.formations.FormationOnisep;
import fr.gouv.monprojetsup.suggestions.infrastructure.onisep.formations.FormationsOnisep;
import fr.gouv.monprojetsup.suggestions.infrastructure.psup.PsupData;
import fr.gouv.monprojetsup.suggestions.poc.ServerData;
import fr.gouv.monprojetsup.suggestions.tools.Serialisation;
import fr.gouv.monprojetsup.suggestions.tools.csv.CsvTools;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.suggestions.tools.Serialisation.fromZippedJson;


/**
 * Classe principale pour l'analyse de la qualité des données du serveur.
 */
@Slf4j
@SpringBootApplication
public class PerformAudit implements CommandLineRunner {

    private final DataSources dataSources;
    private final ServerData serverData;

    @Autowired
    public PerformAudit(
            DataSources dataSources,
            ServerData serverData
            ) {
        this.dataSources = dataSources;
        this.serverData = serverData;
    }

    public static void main(String[] args) {
        SpringApplication.run(PerformAudit.class, args);
    }


    record Theme(
            String cle,
            String intitule,
            List<Theme> sons
    ) {

        public long size() {
            return 1 + sons.stream().mapToLong(Theme::size).sum();
        }

        Theme(
                String cle,
                String intitule
        ) {
            this(cle,intitule,new ArrayList<>());
        }
    }

    record OnisepUrl(
            String flCod,
            String libelle,
            String url,
            List<String> codesIdeos) {

    }

    @Override
    public void run(String... args) throws Exception {

        compareActionsFormations();

        exportCentresDinteretsEtThemes();

        exportTypesFormationsPsup();

        outputResumesDescriptifsFormationsEtMetiersExtraits();

        exportLiensDomainesMetiers();

        exportDataHeleneBaffin();

        exportAttendus();

        Serialisation.toJsonFile("thematiques.json", serverData.thematiques().thematiques(), true);

        /* *** items sans descriptis  ****/
        val desc = serverData.getDescriptifs();

        outputMetiersSansDescriptifs(desc);

        comparerMoyennesBacsEtSco();

        Map<String, Set<String>> regroupements = new TreeMap<>();
        ServerData.flGroups.forEach((s, s2) -> {
            String l = serverData.getLabel(s, s) + " (" + s + ")";
            String l2 = serverData.getLabel(s2, s2) + " (" + s2 + ")";
            Set<String> set = regroupements.computeIfAbsent(l2, z -> new TreeSet<>());
            set.add(l);
            set.add(l2);
        });
        Serialisation.toJsonFile("regroupements.json", regroupements, true);


        /* ******************** JSON OUTPUT OF TAGS SOURCES **********************************/
        Map<String, Set<String>> tagsSourcesWithLabels = new HashMap<>();
        TagsSources tagsSources = TagsSources.loadTagsSources(serverData.getCorrespondances(), dataSources);
        tagsSources.sources().forEach((s, strings) -> {
            Set<String> labels = strings.stream().map(serverData::getLabel).collect(Collectors.toSet());
            tagsSourcesWithLabels.put(s, labels);
        });
        Serialisation.toJsonFile("tagsSourcesWithLabels.json", tagsSourcesWithLabels, true);

        /* ******************** JSON OUTPUT OF INNER DATA OF STATISTIQUES **********************************/
        Serialisation.toJsonFile("nomsfilieresFromStatistiques.json", serverData.nomsFilieres(), true);

        Serialisation.toJsonFile("frontAllLabels.json", serverData.getLabels(), true);

        Serialisation.toJsonFile("frontFormationsLabels.json",
                serverData.getLabels().entrySet()
                        .stream().filter(e -> Helpers.isFiliere(e.getKey()))
                        .filter(e -> !ServerData.flGroups.containsKey(e.getKey()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ))
                , true);

        Serialisation.toJsonFile("frontMetiersLabels.json",
                serverData.getLabels().entrySet()
                        .stream().filter(e -> Helpers.isMetier(e.getKey()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ))
                , true);

        /* ******************** liste des LAS **********************************/
        Map<String, Pair<String, Integer>> las = new TreeMap<>();


        serverData.getFilieres().forEach(filiere -> {
            if (filiere.isLas) {
                int flcod = filiere.cleFiliere;
                int capacite = ServerData.getFormationsFromFil(Constants.gFlCodToFrontId(flcod))
                        .stream().filter(f -> serverData.las().contains(f.gTaCod))
                        .mapToInt(f -> f.capacite)
                        .sum();
                String key = Constants.gFlCodToFrontId(flcod);
                las.put(key, Pair.of(serverData.getLabel(key, key), capacite));
            }
        });
        Serialisation.toJsonFile("las.json", las, true);

        /* ******************** JSON OUTPUT OF TAXONOMIE **********************************/
        //on crée la taxonomie
        Map<String, Theme> indexTheme = new HashMap<>();
        val thematiques = serverData.thematiques();
        thematiques.thematiques().forEach((s, s2) -> indexTheme.computeIfAbsent(s, z -> new Theme(s, s2)));
        Set<String> roots = new HashSet<>(indexTheme.keySet());
        thematiques.parents().forEach((s, l) ->
                l.forEach(s2 -> {
                    Theme t = indexTheme.get(Constants.cleanup(s));
                    roots.remove(Constants.cleanup(s));
                    Theme t2 = indexTheme.get(Constants.cleanup(s2));
                    if (t2 != null) {
                        t2.sons.add(t);
                    }
                }));
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
        PsupToOnisepLines billyLines = Serialisation.fromJsonFile(dataSources.getSourceDataFilePath(
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
                                serverData.getLabel(Constants.FILIERE_PREFIX + e.getKey()),
                                serverData.liensOnisep().get(Constants.FILIERE_PREFIX + e.getKey()),
                                e.getValue()
                        ))
                        .sorted(Comparator.comparingInt(o -> Integer.parseInt(o.flCod.substring(2))))
                        .toList();


        Serialisation.toJsonFile("flcod_to_ideos_et_urls_onisep.json", urls, true);


        /* *** stats fichier xml **/
        FormationsOnisep formationsOnisep = Serialisation.fromJsonFile(
                dataSources.getSourceDataFilePath(DataSources.ONISEP_FORMATIONS_PATH),
                FormationsOnisep.class
        );
        Set<String> knownFromXML = formationsOnisep.formations().stream().map(FormationOnisep::identifiant).collect(Collectors.toSet());
        Set<String> knownFromIdeoHoline = lines.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        knownFromIdeoHoline.retainAll(knownFromXML);

        FilieresToFormationsOnisep filieres = serverData.filieresToFormationsOnisep();

        Serialisation.toJsonFile("formationsOnisepDansLaCorr.json", filieres, true);
        Set<String> connues = filieres.filieres().stream().map(FilieresToFormationsOnisep.FiliereToFormationsOnisep::gFlCod).collect(Collectors.toSet());
        serverData.nomsFilieres().forEach((key, value) -> {
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
        serverData.filActives().forEach(flCod -> {
            String key = Constants.gFlCodToFrontId(flCod);
            if (!desc.keyToDescriptifs().containsKey(key)) {
                String label = serverData.getDebugLabel(key);
                if (!label.contains("null")) {
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



        /*
        // *** correspondances filieres vers Metiers
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

        // chargement et groupement par clé
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
        // suppression des métiers déjà récupérés via onisep
        filToMet2.forEach((s, strings) -> strings.removeAll(corrFoilMet.getOrDefault(s, Collections.emptySet())));
        Map<String, List<String> > filToMet3 =
                filToMet2.entrySet().stream()
                        .collect(Collectors.toMap(
                                e -> ServerData.getLabel(e.getKey()) + " (" + e.getKey() + ")",
                                e -> e.getValue().stream().map(f -> ServerData.getLabel(Constants.cleanup(f)) + " (" + f + ")").toList()
                        ));
        Serialisation.toJsonFile("formationsVersMetiersAjoutManuel.json", filToMet3, true);

        ****/


        Set<String> keys = new HashSet<>();
        keys.addAll(serverData.displayedFilieres());
        keys.addAll(serverData.idMetiersOnisep());

        List<String> itemsWithNoDescriptif = keys.stream().filter(key ->
                        !desc.keyToDescriptifs().containsKey(key)
                                && ServerData.flGroups.getOrDefault(key, key).equals(key)
                                && !key.startsWith(Constants.CENTRE_INTERETS_ROME)
                                && !key.startsWith(Constants.CENTRE_INTERETS_ONISEP)
                                && !key.startsWith(Constants.THEME_PREFIX)
                )
                .map(
                        key -> serverData.getLabel(key, key)
                ).sorted().toList();


        Serialisation.toJsonFile("itemsWithNoDescriptifs.json", itemsWithNoDescriptif, true);

        Serialisation.toJsonFile("descriptifs.json", desc, true);

        performEDSAnalysis();
    }

    private void performEDSAnalysis() throws IOException {

            AttendusDetailles analyses = serverData.getAttendusDetaills();

            Serialisation.toJsonFile("EDS_quali_vs_quanti.json", analyses, true);

            Serialisation.toJsonFile("eds.json", analyses.getSimplifiedFrontVersion(), true);

    }


    private void exportDataHeleneBaffin() throws IOException {
        exportCsvMetierToFiliereCorr("ajouts_metiers_filieres_avec_heritage.csv");
        exportCsvMetierToFiliereCorr("ajouts_metiers_filieres_sans_heritage.csv");
    }

    private void exportTypesFormationsPsup() throws IOException {
        Serialisation.toJsonFile("typesFormationsPsup.json",
                new TreeMap<>(serverData.getTypesMacros()),
                true
        );


    }

    private void exportCsvMetierToFiliereCorr(String s) throws IOException {
        val data = CsvTools.readCSV(s, ',');
        List<List<String>> lines = new ArrayList<>();
        for (Map<String, String> line : data) {
            String key1 = line.get("key1");
            String key2 = line.get("key2");
            if (Helpers.isMetier(key1) && Helpers.isFiliere(key2)) {
                String lib1 = serverData.getLabel(key1, key1);
                String lib2 = serverData.getLabel(key2, key2);
                lines.add(List.of(lib1, key1.replace("_", "."), lib2, key2));
            }
        }
        lines.sort(Comparator.comparing(l -> l.get(0).toLowerCase() + l.get(2).toLowerCase()));

        try (CsvTools csv = new CsvTools(s.replace(".csv", "_hr.csv"), ',')) {
            csv.appendHeaders(List.of("libellé métier", "id_metier", "fl_lib", "fl_cod"));
            for (List<String> strings : lines) {
                csv.append(strings.get(0));
                csv.append(strings.get(1));
                csv.append(strings.get(2));
                csv.append(strings.get(3));
                csv.newLine();
            }

        }

    }

    private void compareActionsFormations() throws IOException {
        List<ActionFormationOni> actionsOni = Serialisation.fromJsonFile(
                dataSources.getSourceDataFilePath(DataSources.ONISEP_ACTIONS_FORMATIONS_PATH),
                new TypeToken<List<ActionFormationOni>>() {
                }.getType()
        );

        JsonCarte carte = Serialisation.fromJsonFile(
                dataSources.getSourceDataFilePath(DataSources.CARTE_JSON_PATH),
                JsonCarte.class
        );

        PsupToOnisepLines lines = Serialisation.fromJsonFile(
                dataSources.getSourceDataFilePath(
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
                Map<String, List<FormationCarte>> psupUAI = carte.formations.values().stream()
                        .filter(f -> f.getFl() == gFlCod)
                        .collect(
                                Collectors.groupingBy(
                                        FormationCarte::getGea
                                )
                        );
                //on calcule les uai des établissements de ce gFlCod, côté onisep

                Map<String, List<ActionFormationOni>> onisepUai = actionsOni.stream()
                        .filter(f -> idsIdeo2.contains(f.getIdOnisep()))
                        .collect(Collectors.groupingBy(ActionFormationOni::ens_code_uai));

                psupUAI.keySet().removeAll(onisepUai.keySet());
                if (psupUAI.isEmpty()) continue;

                for (List<FormationCarte> forms : psupUAI.values()) {

                    for (FormationCarte f : forms) {
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

    private void exportCentresDinteretsEtThemes() throws IOException {
        Interets interets = serverData.interets();
        try (CsvTools csv = new CsvTools("centresInterets.csv", ',')) {
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
        Thematiques thematiques = serverData.thematiques();
        try (CsvTools csv = new CsvTools("thematiques.csv", ',')) {
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

    private void exportAttendus() throws IOException {
        val attendus = serverData.getAttendusParGroupes();
        try (CsvTools csv = new CsvTools("attendus.csv", ',')) {
            csv.appendHeaders(List.of("type", "énonce", "formations"));
            attendus.forEach((stringStringPair, strings) -> {
                try {
                    csv.append(stringStringPair.getLeft());
                    csv.append(stringStringPair.getRight());
                    csv.append(String.join("\n", strings));
                    csv.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void exportLiensDomainesMetiers() throws IOException {
        val liensDomainesMetiers = serverData.getLiensDomainesMetiers();
        Map<String, List<String>> liensDomainesNomsMetiers
                = liensDomainesMetiers.entrySet().stream()
                .flatMap(e -> e.getValue().stream().map(f -> Pair.of(e.getKey(), f)))
                .collect(Collectors.groupingBy(p -> p.getLeft().domaine_onisep()))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().map(p -> serverData.getDebugLabel(p.getRight())).toList())
                );
        Serialisation.toJsonFile("liensDomainesNomsMetiers.json", liensDomainesNomsMetiers, true);

        Map<String, List<String>> liensSousDomainesNomsMetiers
                = liensDomainesMetiers.entrySet().stream()
                .flatMap(e -> e.getValue().stream().map(f -> Pair.of(e.getKey(), f)))
                .collect(Collectors.groupingBy(p -> p.getLeft().sous_domaine_onisep()))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().map(p -> serverData.getDebugLabel(p.getRight())).toList())
                );
        Serialisation.toJsonFile("liensSousDomainesNomsMetiers.json", liensSousDomainesNomsMetiers, true);


    }

    private void comparerMoyennesBacsEtSco() throws IOException {
        /* * comparaison moyennes **/
        Map<String, Pair<Triple<Double, Double, Double>, Triple<Double, Double, Double>>> moyennes = getStatsMoyGenVsBac();
        Serialisation.toJsonFile("compMoyennes.json",
                moyennes,
                true
        );
        try (CsvTools moyennesCsvWriter = new CsvTools("moyennes.csv", ',')) {
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

    private Map<String, Pair<Triple<Double, Double, Double>, Triple<Double, Double, Double>>> getStatsMoyGenVsBac() {
        Set<String> displayedFilieres = serverData.displayedFilieres();
        return displayedFilieres
                .stream()
                .distinct()
                .filter(key -> serverData.getLabel(key) != null)
                .collect(Collectors.toMap(
                        serverData::getLabel,
                        this::getPaireMoyenne

                ));
    }

    private Pair<Triple<Double, Double, Double>, Triple<Double, Double, Double>> getPaireMoyenne(String key) {
        Pair<String, Statistique> statsMoyGen = serverData.getStatsMoyGen(key, PsupStatistiques.TOUS_BACS_CODE);
        Pair<String, Statistique> statsBac = serverData.getStatsBac(key, PsupStatistiques.TOUS_BACS_CODE);
        Triple<Double, Double, Double> moyGen = statsMoyGen == null || statsMoyGen.getRight() == null || statsMoyGen.getRight().nb() < 200 ? null :
                statsMoyGen.getRight().middle50().getTriple();
        Triple<Double, Double, Double> moyBac = statsBac == null || statsBac.getRight() == null || statsBac.getRight().nb() < 200 ? null :
                statsBac.getRight().middle50().getTriple();
        return Pair.of(
                moyGen,
                moyBac
        );
    }

    private void outputMetiersSansDescriptifs(DescriptifsFormations desc) throws IOException {
        ArrayList<String> copy4 = new ArrayList<>();
        serverData.idMetiersOnisep().forEach(key -> {
            if (Helpers.isMetier(key) && !desc.keyToDescriptifs().containsKey(key)) {
                String label = serverData.getDebugLabel(key);
                if (!label.contains("null")) {
                    copy4.add(label);
                }
            }
        });
        Serialisation.toJsonFile("metiers_sans_descriptif.json", copy4, true);

    }

    public void outputResumesDescriptifsFormationsEtMetiersExtraits() throws IOException {

        log.info("Chargement et minimization de " + DataSources.FRONT_MID_SRC_PATH);
        PsupStatistiques data = fromZippedJson(dataSources.getSourceDataFilePath(DataSources.FRONT_MID_SRC_PATH), PsupStatistiques.class);
        data.removeFormations();
        data.removeSmallPopulations();

        log.info("Chargement et nettoyage de " + dataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME));
        PsupData psupData = fromZippedJson(dataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME), PsupData.class);
        psupData.cleanup();

        /*
        log.info("Ajout des liens metiers");
        Map<String, Descriptifs.Link> urls = new HashMap<>();
        data.liensOnisep.forEach((s, s2) -> urls.put(s, new Descriptifs.Link(s2, serverData.getLabel(s))));
        serverData.idMetiersOnisep().forEach(s -> urls.put(s, new Descriptifs.Link("onisep.fr/http/redirections/metier/slug/" + s,s)));
        */
        Map<String, Attendus> eds = serverData.getAttendus();

        //Map<String, GrilleAnalyse> grilles = GrilleAnalyse.getGrilles(psupData);
        val descriptifs = serverData.getDescriptifs();

        val headers = List.of(
                "code filière ou groupe (glcod)",
                "intitulé web",
                "code type formation",
                "intitule type formation",
                "url onisep",
                "URL corrections",
                "url psup",
                DescriptifsFormations.RESUME_FORMATION_V1,
                DescriptifsFormations.RESUME_FORMATION_MPS_HEADER,
                "résumé spécialité",
                "Liens supplémentaires",
                "Retours à Onisep",
                "attendus",
                "recommandations sur le parcours au lycée");
        Set<String> processed = new HashSet<>();

        try (CsvTools csv = new CsvTools("resumesDescriptifsFormations.csv", ',')) {
            csv.appendHeaders(
                    headers
            );

            val lasMpsKeys = psupData.getLasMpsKeys();
            for (String flStr : ServerData.filieresFront) {

                if (lasMpsKeys.contains(flStr)) {
                    continue;
                }
                String label2 = serverData.getLabel(flStr, flStr);
                if (label2.contains("apprentissage") || label2.contains("LAS")) {
                    continue;
                }
                //"code filière ou groupe (glcod)",
                csv.append(flStr);

                processed.add(flStr);

                //"intitulé web",
                csv.append(label2);

                //"code type formation",
                //"intitule type formation",
                int code = Integer.parseInt(flStr.substring(2));
                val formations = serverData.formations();
                if (flStr.startsWith("fr")) {
                    csv.append(flStr);
                    String typeMacro = serverData.typesMacros().getOrDefault(code, "");
                    csv.append(typeMacro);
                } else {
                    Filiere fil = formations.get(code);
                    if (fil == null) {
                        throw new RuntimeException("no data on filiere " + flStr);
                    }
                    csv.append("fr" + fil.gFrCod);
                    String typeMacro = serverData.typesMacros().getOrDefault(fil.gFrCod, "");
                    csv.append(typeMacro);
                }

                val listeFilieres = ServerData.reverseFlGroups.getOrDefault(flStr, Set.of(flStr));

                DescriptifsFormations.DescriptifFormation descriptif = descriptifs.keyToDescriptifs().get(flStr);

                Map<String, String> mpsData = (descriptif == null) ? Map.of() : descriptif.getMpsData();

                //"url onisep",
                csv.append(
                        listeFilieres
                                .stream().map(s ->
                                        Pair.of(
                                                data.liensOnisep.getOrDefault(s, ""),
                                                serverData.getLabel(s)
                                        )
                                )
                                .filter(s -> !s.getLeft().isEmpty())
                                .map(s -> DescriptifsFormations.toAvenirs(s.getLeft(), s.getRight()).uri())
                                .distinct()
                                .collect(Collectors.joining("\n"))
                );

                //"URL corrections",
                csv.append(mpsData.getOrDefault("URL corrections", ""));

                // "url psup",
                csv.append(DescriptifsFormationsMetiers.toParcoursupCarteUrl(listeFilieres));

                //"résumé formation V1",
                if (mpsData.containsKey(DescriptifsFormations.RESUME_FORMATION_V1)) {
                    csv.append(mpsData.getOrDefault(DescriptifsFormations.RESUME_FORMATION_V1, ""));
                } else if (descriptif != null) {
                    String summary = descriptif.getFrontRendering();
                    csv.append(summary);
                } else {
                    csv.append("");
                }

                //                    "résumé formation VLauriane",
                csv.append(mpsData.getOrDefault(DescriptifsFormations.RESUME_FORMATION_MPS_HEADER, ""));

                //                    "résumé spécialité"",
                csv.append(mpsData.getOrDefault("résumé spécialité", ""));

                //                    "Liens supplémentaires"",
                csv.append(mpsData.getOrDefault("Liens supplémentaires", ""));

                //                    "Retours à Onisep",
                csv.append(mpsData.getOrDefault("Retours à Onisep", ""));

                //                    "attendus",
                csv.append(listeFilieres.stream()
                        .map(fl -> Pair.of(fl, eds.get(fl)))
                        .filter(p -> p.getRight() != null && p.getRight().attendus() != null)
                        .map(p -> serverData.getLabel(p.getLeft()) + "\n" + p.getRight().attendus())
                        .filter(o -> !o.isBlank()).distinct()
                        .collect(Collectors.joining("\n\n****    cas multiple    ****\n\n")));

                csv.append(listeFilieres.stream()
                        .map(fl -> Pair.of(fl, eds.get(fl)))
                        .filter(p -> p.getRight() != null && p.getRight().recoEDS() != null)
                        .map(p -> serverData.getLabel(p.getLeft()) + "\n" + p.getRight().recoEDS())
                        .filter(o -> !o.isBlank()).distinct()
                        .collect(Collectors.joining("\n\n****    cas multiple     ****\n\n")));
                csv.newLine();
            }
        }

        try (CsvTools csv = new CsvTools("resumesDescriptifsFormationsSkipped.csv", ',')) {
            csv.appendHeaders(
                    headers
            );
            for (val entry : descriptifs.keyToDescriptifs().entrySet()) {
                String s = entry.getKey();
                if (processed.contains(s)) continue;
                val desc = entry.getValue();
                if (!desc.isViable()) continue;
                val mpsData = desc.getMpsData();
                if (mpsData == null || !mpsData.containsKey(DescriptifsFormations.RESUME_FORMATION_MPS_HEADER) && !mpsData.containsKey("résumé spécialité"))
                    continue;
                for (String h : headers) {
                    csv.append(mpsData.getOrDefault(h, ""));
                }
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

            val lasMpsKeys = psupData.getLasMpsKeys();
            for (String flStr : ServerData.filieresFront) {
                if (lasMpsKeys.contains(flStr)) {
                    continue;
                }
                String label2 = serverData.getLabel(flStr, flStr);
                if (label2.contains("apprentissage") || label2.contains("LAS")) {
                    continue;
                }

                val descriptif = descriptifs.keyToDescriptifs().get(flStr);
                if (descriptif == null || descriptif.hasError() || descriptif.presentation() == null) continue;

                int i = descriptif.presentation().indexOf("Exemples de métiers");
                if (i < 0) continue;
                List<Triple<String, String, Metiers.Metier>> triplets
                        = new ArrayList<>(serverData.metiers().extractMetiers(descriptif.presentation().substring(i)));
                if (triplets.isEmpty()) continue;


                csv.append(flStr);
                csv.append(label2);

                int code = Integer.parseInt(flStr.substring(2));
                if (flStr.startsWith("fr")) {
                    csv.append(flStr);
                    String typeMacro = psupData.formations().typesMacros.getOrDefault(code, "");
                    csv.append(typeMacro);
                } else {
                    Filiere fil = psupData.formations().filieres.get(code);
                    if (fil == null) {
                        throw new RuntimeException("no data on filiere " + flStr);
                    }
                    csv.append("fr" + fil.gFrCod);
                    String typeMacro = serverData.typesMacros().getOrDefault(fil.gFrCod, "");
                    csv.append(typeMacro);
                }

                val listeFilieres = ServerData.reverseFlGroups.getOrDefault(flStr, Set.of(flStr));


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
}

