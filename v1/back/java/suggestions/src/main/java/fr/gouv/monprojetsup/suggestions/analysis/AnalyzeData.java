package fr.gouv.monprojetsup.suggestions.analysis;

import fr.gouv.monprojetsup.data.Helpers;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.analysis.ServerDataAnalysis;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.update.onisep.billy.PsupToOnisepLines;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.data.tools.csv.CsvTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.ServerData.*;
import static fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions.edgesKeys;
import static fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions.edgesLabels;

@Slf4j
public class AnalyzeData {

    public static void main(String[] args) throws Exception {

        AlgoSuggestions.initialize();

        outputRelatedToHealth();

        outputSemanticGraph();

        outputFormationsSansMetiers();

        outputMetiersSansFormations();

        outputFormationsSansThemes();

        ServerDataAnalysis.outputResumesDescriptifsFormationsEtMetiersExtraits();
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
