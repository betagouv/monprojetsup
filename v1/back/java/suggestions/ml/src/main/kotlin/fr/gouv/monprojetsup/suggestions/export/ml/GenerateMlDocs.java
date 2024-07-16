package fr.gouv.monprojetsup.suggestions.export.ml;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.data.Constants;
import fr.gouv.monprojetsup.suggestions.data.DataSources;
import fr.gouv.monprojetsup.suggestions.data.Helpers;
import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
import fr.gouv.monprojetsup.suggestions.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.suggestions.data.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.suggestions.data.tools.Serialisation;
import fr.gouv.monprojetsup.suggestions.data.tools.csv.CsvTools;
import fr.gouv.monprojetsup.suggestions.data.update.onisep.FichesMetierOnisep;
import fr.gouv.monprojetsup.suggestions.data.update.psup.PsupData;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GenerateMlDocs {

    private final SuggestionsData data;

    @Autowired
    public GenerateMlDocs(SuggestionsData data) {
        this.data = data;
    }

    public void outputMlData() throws IOException {

        FichesMetierOnisep fichesMetiers = FichesMetierOnisep.load();

        MetiersScrapped metiersScrapped = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.ONISEP_DESCRIPTIFS_METIERS_PATH),
                MetiersScrapped.class
        );

        Descriptifs descriptifs1 = SuggestionsData.getDescriptifs();

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
                val formations = SuggestionsData.getFormationsFromFil(key);
                formations.forEach(f -> {
                    val descriptif2 = descriptifsIndexedByGta.get(f.gTaCod);
                    if (descriptif2 != null) {
                        texts.add(new MlData.Data("descriptifs_psup_voeu", descriptif2.libVoeu()));
                        texts.add(new MlData.Data("descriptifs_psup_ens", descriptif2.enseignement()));
                        texts.add(new MlData.Data("descriptifs_psup_debouches", descriptif2.debouches()));
                    }
                });
                datas.add(new MlData(key, data.getLabel(key), texts));
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
                datas.add(new MlData(key, data.getLabel(key), texts));
            }

        });
        datas.sort(Comparator.comparing(MlData::key));
        Serialisation.toJsonFile("ml_metiers.json", datas, true);

        Map<String, List<String>> tags = new HashMap<>();
        AlgoSuggestions.edgesKeys.keys().forEach(key -> {
            if (Helpers.isTheme(key) || Helpers.isInteret(key)) {
                tags.computeIfAbsent(data.getLabel(key), k -> new ArrayList<>()).add(key);
            }
        });
        Serialisation.toJsonFile("ml_tags.json", tags, true);


        val gtaToFil = SuggestionsData.getFormationTofilieres();
        List<Set<String>> voeuxParCandidat =
                psupData.voeuxParCandidat().stream().map(
                        voeux -> voeux.stream().map(
                                v -> gtaToFil.get(Constants.FORMATION_PREFIX + v)
                        ).filter(Objects::nonNull).collect(Collectors.toSet())
                ).toList();
        Serialisation.toJsonFile("ml_voeux.json", voeuxParCandidat, true);

    }

    public void outputDoDiff() {
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
                        val labels = vals.stream().map(SuggestionsData::getDebugLabel).sorted().toList();
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
}
