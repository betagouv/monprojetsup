package fr.gouv.monprojetsup.data.model.bacs;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.tools.Serialisation;
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
        try {
            return Serialisation.fromJsonFile(sources.getSourceDataFilePath(DataSources.BACS_FILENAME), type);
        } catch (Exception e) {
            Serialisation.toJsonFile(sources.getSourceDataFilePath(DataSources.BACS_FILENAME),defaultBacs, true);
            return Serialisation.fromJsonFile(sources.getSourceDataFilePath(DataSources.BACS_FILENAME), type);
        }
    }

    private BacsLoader() {

    }
}
