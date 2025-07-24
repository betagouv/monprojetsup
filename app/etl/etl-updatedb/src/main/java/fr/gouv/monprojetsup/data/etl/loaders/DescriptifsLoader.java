package fr.gouv.monprojetsup.data.etl.loaders;

import fr.gouv.monprojetsup.data.model.descriptifs.DescriptifFormation;
import fr.gouv.monprojetsup.data.model.descriptifs.DescriptifsFormationsMetiers;
import fr.gouv.monprojetsup.data.model.metiers.MetierIdeo;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DescriptifsLoader {

     public static void injectFichesMetiers(List<MetierIdeo> fichesMetiers, DescriptifsFormationsMetiers descriptifs) {
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


    public static void addMpsdescriptifsFromFile(
            DescriptifsFormationsMetiers descriptifs,
            @NotNull List<Map<@NotNull String, @NotNull String>> lines,
            String mpsIdKey,
            String genericIdKey,
            String resumeGeneralKey,
            String resumePrincipalKey
    ) {
        Map<String, String> resumesTypesformations = new HashMap<>();
        for (val line : lines) {
            val frCod = line.get(genericIdKey);
            val descFormation = line.get(resumeGeneralKey);
            if(descFormation == null) {
                throw new RuntimeException("No description for " + frCod);
            }
            if (!frCod.isBlank() && !descFormation.isBlank()) {
                resumesTypesformations.put(frCod, descFormation.trim());
            }
        }
        addMpsdescriptifsFromRemoteSheet(descriptifs, lines, resumesTypesformations, mpsIdKey, genericIdKey, resumePrincipalKey);
    }

    public static void addMpsdescriptifsFromRemoteSheet(
            DescriptifsFormationsMetiers descriptifs,
            @NotNull List<Map<@NotNull String, @NotNull String>> lines,
            Map<String,String> resumesTypesformations,
            String mpsIdKey,
            String genericIdKey,
            String descriptionPrincipalKey
    ) {

        for (val line : lines) {
            if(line.values().stream().allMatch(String::isBlank)) continue;
            String mpsCod = line.getOrDefault(mpsIdKey, "");
            if (mpsCod.isBlank()) {
                throw new RuntimeException("Empty key " + mpsIdKey + " in " + line);
            }

            String frcod = line.getOrDefault(genericIdKey, "");

            String descForm = resumesTypesformations.getOrDefault(frcod, "");
            String descFiliere = line.get(descriptionPrincipalKey).trim();

            var descriptif = descriptifs.keyToDescriptifs().computeIfAbsent(mpsCod, z -> new DescriptifFormation(line));
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
