package fr.gouv.monprojetsup.data.update.onisep;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.tools.csv.CsvTools;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ThematiquesOnisep(
        Map<String,ThematiqueOnisep> thematiques,
        List<Pair<String,String>> redirections,
        Map<String, Regroupement> regroupements
) {
    public ThematiquesOnisep() {
        this(
                new HashMap<>(), new ArrayList<>(), new HashMap<>()
        );
    }

    public record Regroupement(
            String label,
            String groupe,

            String emojiGroupe,
            String emoji
    ) {
    }

    public static ThematiquesOnisep load() throws IOException {
        ThematiquesOnisep res = new ThematiquesOnisep();
        //new TypeToken<
        List<ThematiqueOnisep> thematiques = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.THEMATIQUES_PATH),
                new TypeToken<List<ThematiqueOnisep>>(){}.getType()
        );
        thematiques.forEach(res::add);

        String groupe = "";
        String emojig = "";
        for (Map<String, String> stringStringMap : CsvTools.readCSV(DataSources.getSourceDataFilePath(DataSources.THEMATIQUES_REGROUPEMENTS_PATH), '\t')) {
            String id = stringStringMap.get("id").trim();
            String regroupement = stringStringMap.get("regroupement").trim();
            if(!regroupement.isEmpty()) groupe = regroupement;
            String emojiGroupe = stringStringMap.get("Emoji");
            if(!emojiGroupe.isEmpty()) emojig = emojiGroupe;
            String emoji = stringStringMap.get("Emojis");
            String label = stringStringMap.get("label");
            if(groupe.isEmpty()) throw new RuntimeException("Groupe vide dans " + DataSources.THEMATIQUES_REGROUPEMENTS_PATH);
            if(emojig.isEmpty()) throw new RuntimeException("Groupe sans emoji dans " + DataSources.THEMATIQUES_REGROUPEMENTS_PATH);
            res.add(id, label, groupe, emojig, emoji);
        }

        List<ThematiquesOnisep.ThematiqueOnisep> thematiquesNouvelles = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.THEMATIQUES_NOUVELLES_PATH),
                new TypeToken<List<ThematiquesOnisep.ThematiqueOnisep>>(){}.getType()
        );
        thematiquesNouvelles.forEach(res::add);

        List<ThematiquesOnisep.ThematiqueOnisep> thematiquesRedirections = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.THEMATIQUES_REDIRECTIONS_PATH),
                new TypeToken<List<ThematiquesOnisep.ThematiqueOnisep>>(){}.getType()
        );
        thematiquesRedirections.forEach(res::add);
        return res;
    }

    public void add(ThematiqueOnisep item) {
        if(item.nom != null && !item.nom.isEmpty()) {
            thematiques.put(Constants.cleanup(item.id), item);
        }
        if(item.redirection != null && !item.redirection.isEmpty() && !item.redirection.equals("X")) {
            redirections.add(Pair.of(Constants.cleanup(item.id), Constants.cleanup(item.redirection)));
        }
    }

    public void add(String id, String label, String groupe, String emojiGroupe, String emoji) {
        regroupements.put(id, new Regroupement(label, groupe, emojiGroupe, emoji));
    }

    public record ThematiqueOnisep(
            @NotNull String id,
            String nom,
            String parent,

            String redirection
    ) {

    }
}
