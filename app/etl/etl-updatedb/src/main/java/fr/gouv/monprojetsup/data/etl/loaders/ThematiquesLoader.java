package fr.gouv.monprojetsup.data.etl.loaders;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.domain.model.onisep.ThematiquesOnisep;
import fr.gouv.monprojetsup.data.domain.model.thematiques.Thematiques;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ThematiquesLoader {
    public static Thematiques loadThematiquesOnisep(DataSources sources) throws IOException {
        ThematiquesOnisep thematiquesOnisep = ThematiquesOnisepLoader.loadThematiquesOnisep(sources);
        return new Thematiques(thematiquesOnisep);
    }

    public static @NotNull List<Map<@NotNull String, @NotNull String>> loadThematiquesMps(DataSources sources) {
        return CsvTools.readCSV(
                sources.getSourceDataFilePath(DataSources.THEMATIQUES_REGROUPEMENTS_PATH),
                '\t');
    }

    public static class ThematiquesOnisepLoader {
        public static ThematiquesOnisep loadThematiquesOnisep(DataSources sources) throws IOException {
            ThematiquesOnisep res = new ThematiquesOnisep();
            //new TypeToken<
            List<ThematiquesOnisep.ThematiqueOnisep> thematiques = Serialisation.fromJsonFile(
                    sources.getSourceDataFilePath(DataSources.THEMATIQUES_PATH),
                    new TypeToken<List<ThematiquesOnisep.ThematiqueOnisep>>(){}.getType()
            );
            thematiques.forEach(res::add);

            String groupe = "";
            String emojig = "";
            for (Map<String, String> stringStringMap : CsvTools.readCSV(sources.getSourceDataFilePath(DataSources.THEMATIQUES_REGROUPEMENTS_PATH), '\t')) {
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
                    sources.getSourceDataFilePath(DataSources.THEMATIQUES_NOUVELLES_PATH),
                    new TypeToken<List<ThematiquesOnisep.ThematiqueOnisep>>() {}.getType()
            );
            thematiquesNouvelles.forEach(res::add);

            List<ThematiquesOnisep.ThematiqueOnisep> thematiquesRedirections = Serialisation.fromJsonFile(
                    sources.getSourceDataFilePath(DataSources.THEMATIQUES_REDIRECTIONS_PATH),
                    new TypeToken<List<ThematiquesOnisep.ThematiqueOnisep>>(){}.getType()
            );
            thematiquesRedirections.forEach(res::add);
            return res;
        }
    }
}
