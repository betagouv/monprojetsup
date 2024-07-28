package fr.gouv.monprojetsup.data.las;

import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.psup.PsupData;
import lombok.val;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.gouv.monprojetsup.data.Constants.gTaCodToFrontId;

public class LasPassHelpers {
    public static Map<String, String> getGtaToLasMapping(PsupData backPsupData, PsupStatistiques statistiques) {
        val grpToFormations = backPsupData.getGroupesToFormations();
        Set<String> lasCodes = statistiques.getLASCorrespondance().lasToGeneric().keySet();
        Map<String, String> result = new HashMap<>();
        lasCodes.forEach(las -> {
            val formations = grpToFormations.getOrDefault(las, List.of());
            formations.forEach(formation -> {
                result.put(gTaCodToFrontId(formation.gTaCod), las);
            });
        });
        return result;

    }
}
