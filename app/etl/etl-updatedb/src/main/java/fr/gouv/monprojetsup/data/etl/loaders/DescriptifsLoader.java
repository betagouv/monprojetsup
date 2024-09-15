package fr.gouv.monprojetsup.data.etl.loaders;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.model.descriptifs.DescriptifFormation;
import fr.gouv.monprojetsup.data.model.descriptifs.DescriptifsFormationsMetiers;
import fr.gouv.monprojetsup.data.model.metiers.MetierIdeoDuSup;
import fr.gouv.monprojetsup.data.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.data.model.onisep.OnisepData;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import static fr.gouv.monprojetsup.data.Constants.LAS_CONSTANT;
import static fr.gouv.monprojetsup.data.Constants.*;

public class DescriptifsLoader {
    public static @NotNull DescriptifsFormationsMetiers loadDescriptifs(
            OnisepData onisepData,
            Map<String, String> psupKeyToMpsKey,
            Map<String, String> lasToGeneric,
            DataSources sources
    ) throws IOException {


        DescriptifsFormationsMetiers descriptifs =
                Serialisation.fromJsonFile(
                        sources.getSourceDataFilePath(
                                DataSources.ONISEP_SCRAPPED_DESCRIPTIFS_FORMATIONS_PATH
                        ),
                        DescriptifsFormationsMetiers.class
                );

        Map<String, String> summaries = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(
                        DataSources.ONISEP_SCRAPPED_DESCRIPTIFS_FORMATIONS_RESUMES_PATH
                ),
                new TypeToken<>() {
                }.getType()
        );
        descriptifs.inject(summaries);

        MetiersScrapped metiersScrapped = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(
                        DataSources.ONISEP_SCRAPPED_DESCRIPTIFS_METIERS_PATH
                ),
                MetiersScrapped.class);
        descriptifs.inject(metiersScrapped);

        injectFichesMetiers(onisepData.metiersIdeo(), descriptifs);

        addMpsdescriptifs(descriptifs, sources);

        descriptifs.injectGroups(psupKeyToMpsKey);
        descriptifs.injectLas(lasToGeneric);//in this order after groups
        descriptifs.keyToDescriptifs().entrySet().forEach(e -> {
            if(lasToGeneric.containsKey(e.getKey())) {
                val desc = e.getValue();
                e.setValue(DescriptifFormation.addToDescriptif(Constants.PAS_LAS_TEXT, desc));
            }
        });

        return descriptifs;
    }

    private static void injectFichesMetiers(List<MetierIdeoDuSup> fichesMetiers, DescriptifsFormationsMetiers descriptifs) {
        fichesMetiers.forEach(fiche -> {
            String key = fiche.ideo();
            String descriptif = fiche.descriptif();
            descriptifs.inject(
                    key,
                    new DescriptifFormation(
                            descriptif,
                            "",
                            "accroche_metier"
                    ));
        });
    }

    private static void addMpsdescriptifs(DescriptifsFormationsMetiers descriptifs, DataSources sources) {
        val lines = CsvTools.readCSV(
                sources.getSourceDataFilePath(DataSources.RESUMES_MPS_PATH),
                ',');
        String keyFlFr;
        String keyDescFormation;
        String keyDescFiliere;
        String keyTypeFor = "code type formation";

        Map<String, String> resumesTypesformations = new HashMap<>();

        if (lines.isEmpty()) {
            throw new IllegalStateException("No data in " + DataSources.RESUMES_MPS_PATH);
        }
        val line0 = lines.get(0);
        keyFlFr = line0.keySet().stream().filter(k -> k.contains("code")).findAny().orElse(null);
        keyDescFormation = line0.keySet().stream().filter(k -> k.contains("resume type formation")).findAny().orElse(null);
        keyDescFiliere = line0.keySet().stream().filter(k -> k.contains("resume filiere")).findAny().orElse(null);

        val keyUrlCorrection = line0.keySet().stream().filter(k -> k.contains("URL corrections")).findAny().orElse(null);

        val keyUrlSupplementaire = line0.keySet().stream().filter(k -> k.contains("Liens supplémentaires")).findAny().orElse(null);

        val keyRetoursOnisep = line0.keySet().stream().filter(k -> k.contains("Onisep")).findAny().orElse(null);

        if (keyFlFr == null || keyDescFormation == null || keyDescFiliere == null || keyUrlSupplementaire == null || keyUrlCorrection == null || keyRetoursOnisep == null)
            throw new IllegalStateException("No key found in " + line0);

        for (val line : lines) {
            val frCod = line.get(keyTypeFor);
            val descFormation = line.getOrDefault(keyDescFormation, "");
            if (!descFormation.isBlank()) {
                resumesTypesformations.put(frCod, descFormation);
            }
        }

        for (val line : lines) {
            String flfrcod = line.getOrDefault(keyFlFr, "");
            if (flfrcod.isBlank()) {
                throw new RuntimeException("Empty key " + keyFlFr + " in " + line);
            }
            if(flfrcod.equals(Constants.gFlCodToMpsId(LAS_CONSTANT))) {
                continue;
            }

            String frcod = line.getOrDefault(keyTypeFor, "");

            String descForm = line.getOrDefault(keyDescFormation, "");
            if (descForm.isBlank()) {
                descForm = resumesTypesformations.getOrDefault(frcod, "");
            }
            String descFiliere = line.get(keyDescFiliere);

            var descriptif = descriptifs.keyToDescriptifs().computeIfAbsent(flfrcod, z -> new DescriptifFormation(line));
            if (descriptif.getMultiUrls() == null) descriptif.setMultiUrls(new HashSet<>());

            if (!descFiliere.isBlank()) {
                descriptif.setSummary(descFiliere);
                descriptif.setSummaryFormation(descForm);
            } else if (!descForm.isBlank()) {
                descriptif.setSummary(descForm);
            }

            val urlSupplementaire = line.getOrDefault(keyUrlSupplementaire, "").trim();
            if (!urlSupplementaire.isBlank()) {
                descriptif.getMultiUrls().addAll(Arrays.stream(urlSupplementaire.split("\n")).map(String::trim).toList());
            }

            val urlCorrection = line.getOrDefault(keyUrlCorrection, "").trim();
            if (!urlCorrection.isBlank()) {
                descriptif.setCorrectedUrl(true);
                descriptif.getMultiUrls().addAll(Arrays.stream(
                        urlCorrection.split("\n"))
                        .map(String::trim)
                        .map(s -> s.replace(ONISEP_URL1,EXPLORER_AVENIRS_URL))
                        .map(s -> s.replace(ONISEP_URL2,EXPLORER_AVENIRS_URL))
                        .toList()
                );
            }

            descriptif.setMpsData(line);
        }
    }
}
