package fr.gouv.monprojetsup.data.update.onisep;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.tools.Serialisation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ThematiquesOnisep(
        Map<String,ThematiqueOnisep> thematiques,
        Map<String,String> categories,
        Map<String,String> redirections
) {
    public ThematiquesOnisep() {
        this(
                new HashMap<>(), new HashMap<>(), new HashMap<>()
        );
    }

    public static ThematiquesOnisep load() throws IOException {
        ThematiquesOnisep res = new ThematiquesOnisep();
        //new TypeToken<
        List<ThematiqueOnisep> thematiques = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.THEMATIQUES_PATH),
                new TypeToken<List<ThematiqueOnisep>>(){}.getType()
        );
        thematiques.forEach(res::add);

        List<ThematiquesOnisep.ThematiqueOnisep> thematiquesNouvelles = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.THEMATIQUES_NOUVELLES_PATH),
                new TypeToken<List<ThematiquesOnisep.ThematiqueOnisep>>(){}.getType()
        );
        thematiquesNouvelles.forEach(res::add);

        List<ThematiquesOnisep.ThematiqueOnisep> thematiquesCategories = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.THEMATIQUES_CATEGORIES_PATH),
                new TypeToken<List<ThematiquesOnisep.ThematiqueOnisep>>(){}.getType()
        );
        thematiquesCategories.forEach(res::add);

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
        if(item.categorie != null && !item.categorie.isEmpty()) {
            categories.put(Constants.cleanup(item.id), item.categorie);
        }
        if(item.redirection != null && !item.redirection.isEmpty() && !item.redirection.equals("X")) {
            redirections.put(Constants.cleanup(item.id), Constants.cleanup(item.redirection));
        }
    }

    public record ThematiqueOnisep(
            @NotNull String id,
            String nom,
            String parent,

            String categorie,
            String redirection
    ) {

    }
}
