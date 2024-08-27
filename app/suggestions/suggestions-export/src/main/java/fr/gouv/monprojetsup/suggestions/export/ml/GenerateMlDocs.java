package fr.gouv.monprojetsup.suggestions.export.ml;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.domain.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.data.domain.port.CandidatsPort;
import fr.gouv.monprojetsup.data.domain.port.LabelsPort;
import fr.gouv.monprojetsup.data.domain.port.VoeuxPort;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.tools.csv.CsvTools;
import fr.gouv.monprojetsup.data.update.onisep.FichesMetierOnisep;
import fr.gouv.monprojetsup.suggestions.algo.AlgoSuggestions;
import fr.gouv.monprojetsup.data.domain.Helpers;
import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
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
    private final AlgoSuggestions algo;
    private final VoeuxPort voeuxPort;
    private final CandidatsPort candidatsPort;
    private final Map<String, String> labels;

    @Autowired
    public GenerateMlDocs(
            SuggestionsData data,
            AlgoSuggestions algo,
            VoeuxPort voeuxPort,
            LabelsPort labelsPort,
            CandidatsPort candidatsPort) {
        this.data = data;
        this.algo = algo;
        this.voeuxPort = voeuxPort;
        this.candidatsPort = candidatsPort;
        this.labels = labelsPort.retrieveLabels();
    }

    public void outputMlData() throws IOException {

        FichesMetierOnisep fichesMetiers = FichesMetierOnisep.load();

        MetiersScrapped metiersScrapped = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.ONISEP_DESCRIPTIFS_METIERS_PATH),
                MetiersScrapped.class
        );


        val descriptifsIndexedByGta = voeuxPort.retrieveDescriptifs();

        List<MlData> datas = new ArrayList<>();

        val descriptifs1 = voeuxPort.retrieveDescriptifs();

        algo.edgesKeys.keys().forEach(key -> {
            if (Helpers.isFiliere(key)) {
                //ajouter les descriptifs parcoursup
                List<MlData.Data> texts = new ArrayList<>();
                val descriptif1 = descriptifs1.get(key);
                if (descriptif1 != null) {
                    texts.add(new MlData.Data("descriptifs_onisep", descriptif1));
                }
                val voeux = data.getVoeuxIds(key);
                voeux.forEach(voeuId -> {
                    val descriptif2 = descriptifsIndexedByGta.get(voeuId);
                    if (descriptif2 != null) {
                        texts.add(new MlData.Data("descriptifs_psup_voeu", descriptif2.libVoeu()));
                        texts.add(new MlData.Data("descriptifs_psup_ens", descriptif2.enseignement()));
                        texts.add(new MlData.Data("descriptifs_psup_debouches", descriptif2.debouches()));
                    }
                });
                datas.add(new MlData(key, labels.getOrDefault(key, key), texts));
            }

        });
        datas.sort(Comparator.comparing(MlData::key));
        Serialisation.toJsonFile("ml_formations.json", datas, true);

        datas.clear();
        algo.edgesKeys.keys().forEach(key -> {
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
                datas.add(new MlData(key, labels.getOrDefault(key, key), texts));
            }

        });
        datas.sort(Comparator.comparing(MlData::key));
        Serialisation.toJsonFile("ml_metiers.json", datas, true);

        Map<String, List<String>> tags = new HashMap<>();
        algo.edgesKeys.keys().forEach(key -> {
            if (Helpers.isTheme(key) || Helpers.isInteret(key)) {
                tags.computeIfAbsent(labels.getOrDefault(key, key), k -> new ArrayList<>()).add(key);
            }
        });
        Serialisation.toJsonFile("ml_tags.json", tags, true);


        val gtaToFil = data.getFormationToFilieres();
        val voeuxParCandidat = candidatsPort.findAll().stream()
                .map(
                        c -> c.voeux().stream().map(
                                        v -> gtaToFil.get(Constants.FORMATION_PREFIX + v)
                                )
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet())
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
            for (Map<String, String> d : datas) {
                boolean first = true;
                for(val h : headers) {
                    String val = d.get(h);
                    if(first) {
                        csv.append(val);
                        first = false;
                    } else {
                        List<String> vals = gson.fromJson(val, tt);
                        val labelsList = vals.stream().map(x -> labels.getOrDefault(x,x)).toList();
                        csv.append("\"" + String.join("\n", labelsList) + "\"");
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
