package fr.gouv.monprojetsup.suggestions.data.update.onisep;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.suggestions.data.DataSources;
import fr.gouv.monprojetsup.suggestions.data.Helpers;
import fr.gouv.monprojetsup.suggestions.data.ServerData;
import fr.gouv.monprojetsup.suggestions.data.config.DataServerConfig;
import fr.gouv.monprojetsup.suggestions.data.model.Edges;
import fr.gouv.monprojetsup.suggestions.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.suggestions.data.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.suggestions.data.tools.Serialisation;
import fr.gouv.monprojetsup.suggestions.data.tools.csv.CsvTools;
import fr.gouv.monprojetsup.suggestions.data.update.psup.PsupData;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.data.Constants;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions.edgesKeys;

@Slf4j
public class AnalyzeSuggestionsData {

    public static void main(String[] args) throws Exception {

        DataServerConfig.load();

        ServerData.load();

        AlgoSuggestions.initialize();

        outputDoDiff();

        outputMlData();

        outputRelatedToHealth();

        outputGraph();

        outputSemanticGraph();

        outputFormationsSansMetiers();

        outputMetiersSansFormations();

        outputFormationsSansThemes();


    }

    private static void outputDoDiff() {
        String filename = "tags_recommendations_diff.csv";
        val datas = CsvTools.readCSV(filename, ',');
        List<String> headers = List.of("key", "formations", "reco_formations", "common_form", "diff_form_reco", "diff_reco_form");
        val tt = new TypeToken<List<String>>(){}.getType();
        try (CsvTools csv = new CsvTools("tags_recommendations_diff_hr.csv", ';')) {
            csv.appendHeaders(List.of("tag", "reference", "suggestions", "matched","missed", "added"));
            Gson gson = new Gson();
            for (Map<String, String> data : datas) {
                boolean first = true;
                for(val h : headers) {
                    String val = data.get(h);
                    if(first) {
                        csv.append(val);
                        first = false;
                    } else {
                        List<String> vals = gson.fromJson(val, tt);
                        val labels = vals.stream().map(ServerData::getDebugLabel).sorted().toList();
                        csv.append("\"" + String.join("\n", labels) + "\"");
                    }
                }
                csv.newLine();
            }
        } catch (IOException e) {
            log.error("error writing file: {}", filename);
        }
    }

    public record MlData(String key, String label, List<Data> texts) {

        public record Data(String source, Object data) {

        }
    }
    private static void outputMlData() throws IOException {

        FichesMetierOnisep fichesMetiers = FichesMetierOnisep.load();

        MetiersScrapped metiersScrapped = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.ONISEP_DESCRIPTIFS_METIERS_PATH),
                MetiersScrapped.class
        );

        Descriptifs descriptifs1 = ServerData.getDescriptifs();

        val psupData =  Serialisation.fromZippedJson(
                DataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME),
                PsupData.class);

        val descriptifsIndexedByGta = psupData.descriptifsFormations().indexed();


        List<MlData> datas = new ArrayList<>();



        AlgoSuggestions.edgesKeys.keys().forEach(key -> {
            if (Helpers.isFiliere(key)) {
                //ajouter les descriptifs parcoursup
                List<MlData.Data> texts = new ArrayList<>();
                val descriptif1 = descriptifs1.get(key);
                if (descriptif1 != null) {
                    texts.add(new MlData.Data("descriptifs_onisep", descriptif1));
                }
                val formations = ServerData.getFormationsFromFil(key);
                formations.forEach(f -> {
                    val descriptif2 = descriptifsIndexedByGta.get(f.gTaCod);
                    if (descriptif2 != null) {
                        texts.add(new MlData.Data("descriptifs_psup_voeu", descriptif2.libVoeu()));
                        texts.add(new MlData.Data("descriptifs_psup_ens", descriptif2.enseignement()));
                        texts.add(new MlData.Data("descriptifs_psup_debouches", descriptif2.debouches()));
                    }
                });
                datas.add(new MlData(key, ServerData.getLabel(key), texts));
            }

        });
        datas.sort(Comparator.comparing(MlData::key));
        Serialisation.toJsonFile("ml_formations.json", datas, true);

        datas.clear();
        AlgoSuggestions.edgesKeys.keys().forEach(key -> {
            if (Helpers.isMetier(key)) {
                //ajouter les descriptifs parcoursup
                List<MlData.Data> texts = new ArrayList<>();
                val descriptif1 = descriptifs1.get(key);
                //ajouter les descriptifs lauriane
                //ajouter les descriptifs onisep
                //ajouter les descriptifs scrappés
                if (descriptif1 != null) {
                    texts.add(new MlData.Data("descriptifs_onisep", descriptif1));
                }
                //ajouter les descriptifs onisep
                val fiche = fichesMetiers.get(key);
                if (fiche != null) {
                    texts.add(new MlData.Data(DataSources.ONISEP_FICHES_METIERS, fiche));
                }
                //ajouter les descriptifs scrappés
                val scrapped = metiersScrapped.get(key);
                if (scrapped != null) {
                    texts.add(new MlData.Data(DataSources.ONISEP_DESCRIPTIFS_METIERS_PATH, scrapped));
                }
                datas.add(new MlData(key, ServerData.getLabel(key), texts));
            }

        });
        datas.sort(Comparator.comparing(MlData::key));
        Serialisation.toJsonFile("ml_metiers.json", datas, true);

        Map<String, List<String>> tags = new HashMap<>();
        AlgoSuggestions.edgesKeys.keys().forEach(key -> {
            if (Helpers.isTheme(key) || Helpers.isInteret(key)) {
                tags.computeIfAbsent(ServerData.getLabel(key), k -> new ArrayList<>()).add(key);
            }
        });
        Serialisation.toJsonFile("ml_tags.json", tags, true);


        val gtaToFil = ServerData.getFormationTofilieres();
        List<Set<String>> voeuxParCandidat =
                psupData.voeuxParCandidat().stream().map(
                        voeux -> voeux.stream().map(
                                v -> gtaToFil.get(Constants.FORMATION_PREFIX + v)
                        ).filter(Objects::nonNull).collect(Collectors.toSet())
                ).toList();
        Serialisation.toJsonFile("ml_voeux.json", voeuxParCandidat, true);

    }


    private static void outputFormationsSansThemes() throws IOException {

        Serialisation.toJsonFile("formations_sans_themes.json",
                AlgoSuggestions.edgesKeys.edges().entrySet().stream()
                        .filter(e -> Helpers.isFiliere(e.getKey()))
                        .filter(e -> e.getValue().stream().noneMatch(Helpers::isTheme))
                        .map(e -> ServerData.getDebugLabel(e.getKey()))
                        .toList(),
                true);
        Serialisation.toJsonFile("metiers_sans_themes.json",
                AlgoSuggestions.edgesKeys.edges().entrySet().stream()
                        .filter(e -> Helpers.isMetier(e.getKey()))
                        .filter(e -> e.getValue().stream().noneMatch(Helpers::isTheme))
                        .map(e -> ServerData.getDebugLabel(e.getKey()))
                        .toList(),
                true);

        Serialisation.toJsonFile("formations_sans_themes_ni_metier.json",
                AlgoSuggestions.edgesKeys.edges().entrySet().stream()
                        .filter(e -> Helpers.isFiliere(e.getKey()))
                        .filter(e -> e.getValue().stream().noneMatch(f -> Helpers.isTheme(f) || Helpers.isMetier(f)))
                        .map(e -> ServerData.getDebugLabel(e.getKey()))
                        .toList(),
                true);
        Serialisation.toJsonFile("metiers_sans_themes_ni_formation.json",
                AlgoSuggestions.edgesKeys.edges().entrySet().stream()
                        .filter(e -> Helpers.isMetier(e.getKey()))
                        .filter(e -> e.getValue().stream().noneMatch(f -> Helpers.isTheme(f) || Helpers.isFiliere(f)))
                        .map(e -> ServerData.getDebugLabel(e.getKey()))
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

        Map<String, String> billys = new HashMap<>();
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

    private static void outputGraph() throws IOException {
        Serialisation.toJsonFile("graph.json", edgesKeys.edges(), true);
    }
    private static void outputSemanticGraph() throws IOException {
        Edges edgesLabels = new Edges();
        edgesLabels.createLabelledGraphFrom(edgesKeys, ServerData.getLabels());
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
