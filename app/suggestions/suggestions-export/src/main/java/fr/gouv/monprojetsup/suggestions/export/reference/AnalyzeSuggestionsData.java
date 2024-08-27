package fr.gouv.monprojetsup.suggestions.export.reference;

import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.tools.csv.CsvTools;
import fr.gouv.monprojetsup.suggestions.algo.AlgoSuggestions;
import fr.gouv.monprojetsup.data.domain.Helpers;
import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
import fr.gouv.monprojetsup.suggestions.data.model.Edges;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.domain.Helpers.isFiliere;

@Slf4j
@Component
public class AnalyzeSuggestionsData {

    private final SuggestionsData data;
    private final AlgoSuggestions algo;

    @Autowired
    public AnalyzeSuggestionsData(
            SuggestionsData data,
            AlgoSuggestions algo) {
        this.data = data;
        this.algo = algo;
    }

    public void analyze() throws Exception {

        outputRelatedToHealth();

        outputGraph();

        outputSemanticGraph();

        outputFormationsSansMetiers();

        outputMetiersSansFormations();

        outputFormationsSansThemes();


    }


    private void outputFormationsSansThemes() throws IOException {

        Serialisation.toJsonFile("formations_sans_themes.json",
                algo.edgesKeys.edges().entrySet().stream()
                        .filter(e -> isFiliere(e.getKey()))
                        .filter(e -> e.getValue().stream().noneMatch(Helpers::isTheme))
                        .map(e -> data.getDebugLabel(e.getKey()))
                        .toList(),
                true);
        Serialisation.toJsonFile("metiers_sans_themes.json",
                algo.edgesKeys.edges().entrySet().stream()
                        .filter(e -> Helpers.isMetier(e.getKey()))
                        .filter(e -> e.getValue().stream().noneMatch(Helpers::isTheme))
                        .map(e -> data.getDebugLabel(e.getKey()))
                        .toList(),
                true);

        Serialisation.toJsonFile("formations_sans_themes_ni_metier.json",
                algo.edgesKeys.edges().entrySet().stream()
                        .filter(e -> isFiliere(e.getKey()))
                        .filter(e -> e.getValue().stream().noneMatch(f -> Helpers.isTheme(f) || Helpers.isMetier(f)))
                        .map(e -> data.getDebugLabel(e.getKey()))
                        .toList(),
                true);
        Serialisation.toJsonFile("metiers_sans_themes_ni_formation.json",
                algo.edgesKeys.edges().entrySet().stream()
                        .filter(e -> Helpers.isMetier(e.getKey()))
                        .filter(e -> e.getValue().stream().noneMatch(f -> Helpers.isTheme(f) || isFiliere(f)))
                        .map(e -> data.getDebugLabel(e.getKey()))
                        .toList(),
                true);


    }

    private void outputMetiersSansFormations() throws IOException {



        Map<String, String> metiersSansFormations = algo.edgesKeys.edges().entrySet().stream()
                .filter(e -> Helpers.isMetier(e.getKey()))
                .filter(e -> e.getValue().stream().noneMatch(Helpers::isFiliere))
                .map(e -> Pair.of(e.getKey(), data.getDebugLabel(e.getKey())))
                .filter(e -> !e.getRight().contains("null"))
                .collect(Collectors.toMap(
                        Pair::getLeft,
                        Pair::getRight
                ));

        Serialisation.toJsonFile("metiers_sans_formations_psup.json",
                metiersSansFormations.values(),
                true);

        try(CsvTools csv = new CsvTools("metiers_sans_formations.csv",',')) {
            csv.appendHeaders(List.of("id", "label"));
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

    private void outputFormationsSansMetiers() throws IOException {
        Map<String, String> formationsSansMetiers = algo.edgesKeys.edges().entrySet().stream()
                .filter(e -> isFiliere(e.getKey()))
                .filter(e -> e.getValue().stream().noneMatch(Helpers::isMetier))
                .map(e -> Pair.of(e.getKey(), data.getDebugLabel(e.getKey())))
                .filter(e -> !e.getRight().contains("groupe") && !e.getRight().contains("null"))
                .collect(Collectors.toMap(
                        Pair::getLeft,
                        Pair::getRight
                ));
        Serialisation.toJsonFile("formations_sans_metiers.json",
                formationsSansMetiers.values(),
                true);

        Map<String, String> billys =  new HashMap<>();
        /*onisepData.billy().psupToIdeo2().stream()
                .collect(Collectors.toMap(
                        PsupToOnisepLines.PsupToOnisepLine::G_FL_COD,
                        PsupToOnisepLines.PsupToOnisepLine::IDS_IDEO2
                ));
        */
        try(CsvTools csv = new CsvTools("formations_psup_sans_metiers.csv",',')) {
            csv.appendHeaders(List.of("id", "LIS_ID_ONI2","label"));
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

    private void outputGraph() throws IOException {
        Serialisation.toJsonFile("graph.json", algo.edgesKeys.edges(), true);
    }
    private void outputSemanticGraph() throws IOException {
        Edges edgesLabels = new Edges();
        edgesLabels.createLabelledGraphFrom(algo.edgesKeys, data.getDebugLabels());
        Serialisation.toJsonFile("semantic_graph.json", edgesLabels, true);
    }

    private void outputRelatedToHealth() throws IOException {
        /* formations liés aux métiers de la santé */
        Serialisation.toJsonFile("relatedToHealth.json",
                algo.getRelatedToHealth()
                        .stream().map(key -> data.getDebugLabel(key))
                        .toList()
                , true);

    }
}
