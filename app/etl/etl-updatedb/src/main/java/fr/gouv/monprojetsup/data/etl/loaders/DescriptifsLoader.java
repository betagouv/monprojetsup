package fr.gouv.monprojetsup.data.etl.loaders;

import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.model.descriptifs.DescriptifFormation;
import fr.gouv.monprojetsup.data.model.descriptifs.DescriptifsFormationsMetiers;
import fr.gouv.monprojetsup.data.model.metiers.MetierIdeo;
import fr.gouv.monprojetsup.data.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.data.model.onisep.OnisepData;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static fr.gouv.monprojetsup.data.Constants.LAS_CONSTANT;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.RESUMES_MPS_RESUME_FORMATION;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.RESUMES_MPS_RESUME_FORMATION_MOS;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.RESUMES_MPS_RESUME_KEY;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.RESUMES_MPS_RESUME_TYPE_FORMATION;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.RESUMES_MPS_RESUME_TYPE_FORMATION_MOS;

public class DescriptifsLoader {
    public static @NotNull DescriptifsFormationsMetiers loadDescriptifs(
            OnisepData onisepData,
            Map<String, String> lasToGeneric,
            DataSources sources
    ) throws IOException {

        DescriptifsFormationsMetiers descriptifs = new DescriptifsFormationsMetiers();

        MetiersScrapped metiersScrapped = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(
                        DataSources.ONISEP_SCRAPPED_DESCRIPTIFS_METIERS_PATH
                ),
                MetiersScrapped.class);
        descriptifs.inject(metiersScrapped);

        injectFichesMetiers(onisepData.metiersIdeo(), descriptifs);

        addMpsdescriptifs(descriptifs, sources);

        //descriptifs.injectGroups(psupKeyToMpsKey);
        descriptifs.injectLas(lasToGeneric);//in this order after groups
        descriptifs.keyToDescriptifs().entrySet().forEach(e -> {
            if(lasToGeneric.containsKey(e.getKey())) {
                val desc = e.getValue();
                e.setValue(DescriptifFormation.addToDescriptif(Constants.PAS_LAS_TEXT, desc));
            }
        });

        return descriptifs;
    }

    private static void injectFichesMetiers(List<MetierIdeo> fichesMetiers, DescriptifsFormationsMetiers descriptifs) {
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
        String keyDescFormation = RESUMES_MPS_RESUME_TYPE_FORMATION;
        String keyDescFiliere = RESUMES_MPS_RESUME_FORMATION;
        String keyTypeFor = "code type formation";

        Map<String, String> resumesTypesformations = new HashMap<>();

        if (lines.isEmpty()) {
            throw new IllegalStateException("No data in " + DataSources.RESUMES_MPS_PATH);
        }
        for (val line : lines) {
            val frCod = line.get(keyTypeFor);
            val descFormation = line.get(RESUMES_MPS_RESUME_TYPE_FORMATION);
            val descFormationMos = line.get(RESUMES_MPS_RESUME_TYPE_FORMATION_MOS);
            if(descFormation == null || descFormationMos == null) {
                throw new RuntimeException("No description for " + frCod);
            }
            if (!frCod.isBlank() && !descFormationMos.isBlank()) {
                resumesTypesformations.put(frCod, descFormationMos.trim());
            } else if (!frCod.isBlank() && !descFormation.isBlank()) {
                resumesTypesformations.put(frCod, descFormation.trim());
            }
        }

        for (val line : lines) {
            if(line.values().stream().allMatch(String::isBlank)) continue;
            String flfrcod = line.getOrDefault(RESUMES_MPS_RESUME_KEY, "");
            if (flfrcod.isBlank()) {
                throw new RuntimeException("Empty key " + RESUMES_MPS_RESUME_KEY + " in " + line);
            }
            if(flfrcod.equals(Constants.gFlCodToMpsId(LAS_CONSTANT))) {
                continue;
            }

            String frcod = line.getOrDefault(keyTypeFor, "");

            String descForm = resumesTypesformations.getOrDefault(frcod, "");
            String descFiliere = line.get(RESUMES_MPS_RESUME_FORMATION_MOS).trim();
            if(descFiliere.isBlank()) {
                descFiliere = line.get(RESUMES_MPS_RESUME_FORMATION).trim();
            }

            var descriptif = descriptifs.keyToDescriptifs().computeIfAbsent(flfrcod, z -> new DescriptifFormation(line));
            if (descriptif.getMultiUrls() == null) descriptif.setMultiUrls(new HashSet<>());

            if (!descFiliere.isBlank()) {
                descriptif.setSummary(descFiliere);
                descriptif.setSummaryFormation(descForm);
            } else if (!descForm.isBlank()) {
                descriptif.setSummary(descForm);
            }

            descriptif.setMpsData(line);
        }
    }
}
