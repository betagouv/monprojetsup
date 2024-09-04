package fr.gouv.monprojetsup.data.etl.loaders;

import fr.gouv.monprojetsup.data.domain.model.onisep.DomainesMps;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class DomainesMpsLoader {

    public static @NotNull List<Map<@NotNull String, @NotNull String>> loadDomainesMps(DataSources sources) {
        return CsvTools.readCSV(
                sources.getSourceDataFilePath(DataSources.DOMAINES_MPS_PATH),
                '\t');
    }

    public static DomainesMps load(DataSources sources) {
        DomainesMps res = new DomainesMps();

        String groupe = "";
        String emojig = "";
        for (Map<String, String> stringStringMap : CsvTools.readCSV(sources.getSourceDataFilePath(DataSources.DOMAINES_MPS_PATH), '\t')) {
            String id = stringStringMap.get("id").trim();
            String regroupement = stringStringMap.get("regroupement").trim();
            if (!regroupement.isEmpty()) groupe = regroupement;
            String emojiGroupe = stringStringMap.get("Emoji");
            if (!emojiGroupe.isEmpty()) emojig = emojiGroupe;
            String emoji = stringStringMap.get("Emojis");
            String label = stringStringMap.get("label");
            if (groupe.isEmpty()) throw new RuntimeException("Groupe vide dans " + DataSources.DOMAINES_MPS_PATH);
            if (emojig.isEmpty()) throw new RuntimeException("Groupe sans emoji dans " + DataSources.DOMAINES_MPS_PATH);
            res.add(id, label, groupe, emojig, emoji);
        }

        return res;
    }
}
