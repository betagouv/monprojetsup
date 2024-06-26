package fr.gouv.monprojetsup.suggestions.analysis;

import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.Helpers;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.config.DataServerConfig;
import fr.gouv.monprojetsup.data.model.Edges;
import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.data.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.tools.csv.CsvTools;
import fr.gouv.monprojetsup.data.update.UpdateFrontData;
import fr.gouv.monprojetsup.data.update.onisep.FichesMetierOnisep;
import fr.gouv.monprojetsup.data.update.onisep.billy.PsupToOnisepLines;
import fr.gouv.monprojetsup.data.update.psup.PsupData;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.FORMATION_PREFIX;
import static fr.gouv.monprojetsup.data.DataSources.BACK_PSUP_DATA_FILENAME;
import static fr.gouv.monprojetsup.data.DataSources.ONISEP_FICHES_METIERS;
import static fr.gouv.monprojetsup.data.Helpers.*;
import static fr.gouv.monprojetsup.data.ServerData.*;
import static fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions.edgesKeys;

@Slf4j
public class AnalyzeSuggestionsData {

    public static void main(String[] args) throws Exception {

        DataServerConfig.load();

        ServerData.load();

        AlgoSuggestions.initialize();

        outputMlData();

        outputRelatedToHealth();

        outputGraph();

        outputSemanticGraph();

        outputFormationsSansMetiers();

        outputMetiersSansFormations();

        outputFormationsSansThemes();


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

        Descriptifs descriptifs1 = UpdateFrontData.DataContainer.loadDescriptifs(
                onisepData,
                backPsupData.getCorrespondances(),
                statistiques.getLASCorrespondance().lasToGeneric()
        );

        val psupData =  Serialisation.fromZippedJson(
                DataSources.getSourceDataFilePath(BACK_PSUP_DATA_FILENAME),
                PsupData.class);

        val descriptifsIndexedByGta = psupData.descriptifsFormations().indexed();


        List<MlData> datas = new ArrayList<>();



        AlgoSuggestions.edgesKeys.keys().forEach(key -> {
            if (isFiliere(key)) {
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
                datas.add(new MlData(key, getLabel(key), texts));
            }

        });
        datas.sort(Comparator.comparing(MlData::key));
        Serialisation.toJsonFile("ml_formations.json", datas, true);

        datas.clear();
        AlgoSuggestions.edgesKeys.keys().forEach(key -> {
            if (isMetier(key)) {
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
                    texts.add(new MlData.Data(ONISEP_FICHES_METIERS, fiche));
                }
                //ajouter les descriptifs scrappés
                val scrapped = metiersScrapped.get(key);
                if (scrapped != null) {
                    texts.add(new MlData.Data(DataSources.ONISEP_DESCRIPTIFS_METIERS_PATH, scrapped));
                }
                datas.add(new MlData(key, getLabel(key), texts));
            }

        });
        datas.sort(Comparator.comparing(MlData::key));
        Serialisation.toJsonFile("ml_metiers.json", datas, true);

        Map<String, List<String>> tags = new HashMap<>();
        AlgoSuggestions.edgesKeys.keys().forEach(key -> {
            if (isTheme(key) || isInteret(key)) {
                tags.computeIfAbsent(getLabel(key), k -> new ArrayList<>()).add(key);
            }
        });
        Serialisation.toJsonFile("ml_tags.json", tags, true);


        val gtaToFil = ServerData.getFormationTofilieres();
        List<Set<String>> voeuxParCandidat =
                psupData.voeuxParCandidat().stream().map(
                        voeux -> voeux.stream().map(
                                v -> gtaToFil.get(FORMATION_PREFIX + v)
                        ).filter(Objects::nonNull).collect(Collectors.toSet())
                ).toList();
        Serialisation.toJsonFile("ml_voeux.json", voeuxParCandidat, true);

    }


    private static void outputFormationsSansThemes() throws IOException {

        Serialisation.toJsonFile("formations_sans_themes.json",
                AlgoSuggestions.edgesKeys.edges().entrySet().stream()
                        .filter(e -> isFiliere(e.getKey()))
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
                        .filter(e -> isFiliere(e.getKey()))
                        .filter(e -> e.getValue().stream().noneMatch(f -> Helpers.isTheme(f) || Helpers.isMetier(f)))
                        .map(e -> ServerData.getDebugLabel(e.getKey()))
                        .toList(),
                true);
        Serialisation.toJsonFile("metiers_sans_themes_ni_formation.json",
                AlgoSuggestions.edgesKeys.edges().entrySet().stream()
                        .filter(e -> Helpers.isMetier(e.getKey()))
                        .filter(e -> e.getValue().stream().noneMatch(f -> Helpers.isTheme(f) || isFiliere(f)))
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
                .filter(e -> isFiliere(e.getKey()))
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
