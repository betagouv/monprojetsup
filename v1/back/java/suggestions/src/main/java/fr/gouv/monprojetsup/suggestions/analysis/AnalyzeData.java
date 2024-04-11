package fr.gouv.monprojetsup.suggestions.analysis;

import fr.gouv.monprojetsup.data.Helpers;
import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.analysis.eds.AnalyzeEDS;
import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.data.model.eds.Attendus;
import fr.gouv.monprojetsup.data.model.formations.Filiere;
import fr.gouv.monprojetsup.data.model.specialites.SpecialitesLoader;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.update.UpdateFrontData;
import fr.gouv.monprojetsup.data.update.onisep.billy.PsupToOnisepLines;
import fr.gouv.monprojetsup.data.update.psup.PsupData;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.data.tools.csv.CsvTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.FILIERE_PREFIX;
import static fr.gouv.monprojetsup.data.Constants.gFlCodToFrontId;
import static fr.gouv.monprojetsup.data.ServerData.*;
import static fr.gouv.monprojetsup.data.tools.Serialisation.fromZippedJson;
import static fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions.edgesKeys;
import static fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions.edgesLabels;
import static fr.parcoursup.carte.algos.Filiere.LAS_CONSTANT;

@Slf4j
public class AnalyzeData {

    public static void main(String[] args) throws Exception {

        AlgoSuggestions.initialize();

        outputRelatedToHealth();

        outputSemanticGraph();

        outputFormationsSansMetiers();

        outputMetiersSansFormations();

        outputFormationsSansThemes();

        outputResumesdescriptifsFormations();
    }

    private static void outputResumesdescriptifsFormations() throws IOException {

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

        Map<String, Attendus> eds = AnalyzeEDS.getEDSSimple(
                psupData,
                data,
                SpecialitesLoader.load(),
                false
        );

        UpdateFrontData.DataContainer data2 = UpdateFrontData.DataContainer.load(
                psupData,
                onisepData,
                urls,
                data.getLASCorrespondance(),
                eds);

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


    private static void outputFormationsSansThemes() throws IOException {

        Serialisation.toJsonFile("formations_sans_themes.json",
                AlgoSuggestions.edgesKeys.edges().entrySet().stream()
                        .filter(e -> Helpers.isFiliere(e.getKey()))
                        .filter(e -> e.getValue().stream().noneMatch(Helpers::isTheme))
                        .map(e -> ServerData.getDebugLabel(e.getKey()))
                        .filter(e -> !e.contains("groupe"))
                        .toList(),
                true);
        Serialisation.toJsonFile("formations_sans_themes_ni_metier.json",
                AlgoSuggestions.edgesKeys.edges().entrySet().stream()
                        .filter(e -> Helpers.isFiliere(e.getKey()))
                        .filter(e -> e.getValue().stream().noneMatch(f -> Helpers.isTheme(f) || Helpers.isMetier(f)))
                        .map(e -> ServerData.getDebugLabel(e.getKey()))
                        .filter(e -> !e.contains("groupe"))
                        .toList(),
                true);


    }

    private static void outputMetiersSansFormations() throws IOException {



        Map<String, String> metiersSansFormations = edgesKeys.edges().entrySet().stream()
                .filter(e -> Helpers.isMetier(e.getKey()))
                .filter(e -> e.getValue().stream().noneMatch(Helpers::isFiliere))
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

    }

    private static void outputFormationsSansMetiers() throws IOException {
        Map<String, String> formationsSansMetiers = edgesKeys.edges().entrySet().stream()
                .filter(e -> Helpers.isFiliere(e.getKey()))
                .filter(e -> e.getValue().stream().noneMatch(Helpers::isMetier))
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

    }

    private static void outputSemanticGraph() throws IOException {
        edgesLabels.createLabelledGraphFrom(edgesKeys, statistiques.labels);

        Serialisation.toJsonFile("semantic_graph.json", edgesLabels, true);

    }

    private static void outputRelatedToHealth() throws IOException {
        /* formations liés aux métiers de la santé */
        Serialisation.toJsonFile("relatedToHealth.json",
                AlgoSuggestions.getRelatedToHealth()
                        .stream().map(ServerData::getDebugLabel)
                        .toList()
                , true);

    }
}
