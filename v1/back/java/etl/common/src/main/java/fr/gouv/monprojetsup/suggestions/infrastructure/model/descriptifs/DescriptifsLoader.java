package fr.gouv.monprojetsup.suggestions.infrastructure.model.descriptifs;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.suggestions.domain.Constants;
import fr.gouv.monprojetsup.suggestions.infrastructure.DataSources;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.suggestions.infrastructure.onisep.FichesMetierOnisep;
import fr.gouv.monprojetsup.suggestions.infrastructure.onisep.OnisepData;
import fr.gouv.monprojetsup.suggestions.infrastructure.onisep.SecteursPro;
import fr.gouv.monprojetsup.suggestions.tools.Serialisation;
import fr.gouv.monprojetsup.suggestions.tools.csv.CsvTools;
import lombok.val;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DescriptifsLoader {
    public static DescriptifsFormations loadDescriptifs(
            OnisepData onisepData,
            Map<String, String> psupKeyToMpsKey,
            Set<String> lasFormations,
            DataSources sources
    ) throws IOException {

        FichesMetierOnisep fichesMetiers = FichesMetierOnisep.loadFichesMetierOnisep(sources);
        fichesMetiers.metiers().metier().removeIf(fiche ->
                fiche.identifiant() == null
                        || !onisepData.metiers().metiers().containsKey(Constants.cleanup(fiche.identifiant()))
        );

        DescriptifsFormations descriptifs =
                Serialisation.fromJsonFile(
                        sources.getSourceDataFilePath(
                                DataSources.ONISEP_DESCRIPTIFS_FORMATIONS_PATH
                        ),
                        DescriptifsFormations.class
                );

        Map<String, String> summaries = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(
                        DataSources.ONISEP_DESCRIPTIFS_FORMATIONS_RESUMES_PATH
                ),
                new TypeToken<>() {
                }.getType()
        );
        descriptifs.inject(summaries);

        MetiersScrapped metiersScrapped = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(
                        DataSources.ONISEP_DESCRIPTIFS_METIERS_PATH
                ),
                MetiersScrapped.class);
        descriptifs.inject(metiersScrapped);

        injectFichesMetiers(fichesMetiers, descriptifs);

        injectDomainesPro(onisepData.domainesWeb(), descriptifs);

        boolean includeMpsData = true;
        if (includeMpsData) {
            addMpsdescriptifs(descriptifs, sources);
        }

        descriptifs.injectGroups(psupKeyToMpsKey);
        descriptifs.keyToDescriptifs().entrySet().forEach(e -> {
            if(lasFormations.contains(e.getKey())) {
                val desc = e.getValue();
                e.setValue(DescriptifsFormations.DescriptifFormation.addToDescriptif(Constants.PAS_LAS_TEXT, desc));
            }
        });

        return descriptifs;
    }

    private static void injectDomainesPro(SecteursPro secteursPro, DescriptifsFormations descriptifs) {
        secteursPro.domaines().forEach(domainePro -> descriptifs.inject(
                        Constants.cleanup(domainePro.id()),
                        new DescriptifsFormations.DescriptifFormation(
                                domainePro.description(),
                                domainePro.url(),
                                "domainePro")
                )
        );

    }

    private static void injectFichesMetiers(FichesMetierOnisep fichesMetiers, DescriptifsFormations descriptifs) {
        //inject descriptifs
        //inject urls
        fichesMetiers.metiers().metier().forEach(fiche -> {
            String key = Constants.cleanup(fiche.identifiant());
            String descriptif = fiche.accroche_metier();
            if (descriptif == null) {
                Map<String, List<FichesMetierOnisep.FormatCourt>> textes
                        = fiche.formats_courts().format_court().stream().collect(Collectors.groupingBy(FichesMetierOnisep.FormatCourt::type));
                List<FichesMetierOnisep.FormatCourt> accroches = textes.get("accroche_metier");
                if (accroches != null && !accroches.isEmpty()) {
                    FichesMetierOnisep.FormatCourt accroche = accroches.get(0);
                    if (accroche.descriptif() != null) {
                        descriptif = accroche.descriptif();
                    }
                }
            }
            descriptifs.inject(
                    key,
                    new DescriptifsFormations.DescriptifFormation(
                            descriptif,
                            "",
                            "accroche_metier"
                    ));
        });
    }

    private static void addMpsdescriptifs(DescriptifsFormations descriptifs, DataSources sources) {
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
        keyDescFormation = line0.keySet().stream().filter(k -> k.contains("formation VLauriane")).findAny().orElse(null);
        keyDescFiliere = line0.keySet().stream().filter(k -> k.contains("spécialité")).findAny().orElse(null);

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

            String frcod = line.getOrDefault(keyTypeFor, "");

            String descForm = line.getOrDefault(keyDescFormation, "");
            if (descForm.isBlank()) {
                descForm = resumesTypesformations.getOrDefault(frcod, "");
            }
            String descFiliere = line.get(keyDescFiliere);

            var descriptif = descriptifs.keyToDescriptifs().computeIfAbsent(flfrcod, z -> new DescriptifsFormations.DescriptifFormation(line));
            if (descriptif.getMultiUrls() == null) descriptif.setMultiUrls(new HashSet<>());

            if (!descFiliere.isBlank()) {
                descriptif.setSummary(descFiliere);
                descriptif.setSummaryFormation(descForm);
            } else if (!descForm.isBlank()) {
                descriptif.setSummary(descForm);
            }

            val urlSupplementaire = line.getOrDefault(keyUrlSupplementaire, "");
            if (!urlSupplementaire.isBlank()) {
                descriptif.getMultiUrls().addAll(Arrays.stream(urlSupplementaire.split("\n")).map(String::trim).toList());
            }

            val urlCorrection = line.getOrDefault(keyUrlCorrection, "");
            if (!urlCorrection.isBlank()) {
                descriptif.setCorrectedUrl(true);
                descriptif.getMultiUrls().addAll(Arrays.stream(urlCorrection.split("\n")).map(String::trim).toList());
            }

            descriptif.setMpsData(line);
        }
    }
}
