package fr.gouv.monprojetsup.data.model.bacs;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.DataSources;
import lombok.val;

import java.io.IOException;
import java.util.List;

import static fr.gouv.monprojetsup.data.tools.Serialisation.fromJsonFile;
import static fr.gouv.monprojetsup.data.tools.Serialisation.toJsonFile;

public class BacsLoader {

    public static List<Bac> defaultBacs = List.of(
            new Bac("sec",  "Seconde"),
            new Bac("prem",  "Première"),
            new Bac("term",  "Terminale")
    );

    public static List<Bac> load() throws IOException {
        val type = new TypeToken<List<Bac>>() {}.getType();
        try {
            return fromJsonFile(DataSources.getSourceDataFilePath(DataSources.BACS_FILENAME), type);
        } catch (Exception e) {
            toJsonFile(DataSources.getSourceDataFilePath(DataSources.BACS_FILENAME),defaultBacs, true);
            return fromJsonFile(DataSources.getSourceDataFilePath(DataSources.BACS_FILENAME), type);
        }
    }

    private BacsLoader() {

    }
}
