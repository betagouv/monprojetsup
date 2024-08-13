package fr.gouv.monprojetsup.suggestions.infrastructure.model.bacs;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.suggestions.infrastructure.DataSources;
import fr.gouv.monprojetsup.suggestions.tools.Serialisation;
import lombok.val;

import java.io.IOException;
import java.util.List;

public class BacsLoader {

    public static List<Bac> defaultBacs = List.of(
            new Bac("sec",  "Seconde"),
            new Bac("prem",  "Première"),
            new Bac("term",  "Terminale")
    );

    public static List<Bac> load(DataSources sources) throws IOException {
        val type = new TypeToken<List<Bac>>() {}.getType();
        return Serialisation.fromJsonFile(sources.getSourceDataFilePath(DataSources.BACS_FILENAME), type);
    }

    private BacsLoader() {

    }
}
