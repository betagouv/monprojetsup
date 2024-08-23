package fr.gouv.monprojetsup.data.infrastructure.model.bacs;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.infrastructure.DataSources;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class BacsLoader {


    public static @NotNull List<@NotNull Bac> load(DataSources sources) throws IOException {
        val type = new TypeToken<List<Bac>>() {}.getType();
        return Serialisation.fromJsonFile(sources.getSourceDataFilePath(DataSources.BACS_FILENAME), type);
    }

    private BacsLoader() {

    }
}
