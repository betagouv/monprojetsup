package fr.gouv.monprojetsup.data.etl.loaders;

import fr.gouv.monprojetsup.data.model.psup.SpeBac;
import fr.gouv.monprojetsup.data.model.specialites.Specialites;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

import static fr.gouv.monprojetsup.data.tools.Serialisation.fromJsonFile;

public class SpecialitesLoader {

      public static @NotNull Specialites load(
              @NotNull DataSources sources,
              @NotNull Collection<@NotNull SpeBac> spesBacs) throws IOException {

        Specialites specsFromFiles =
            fromJsonFile(sources.getSourceDataFilePath(DataSources.SPECIALITES_FILENAME), Specialites.class);

        return Specialites.build(
                specsFromFiles,
                spesBacs
        );
    }

    private SpecialitesLoader() {

    }
}
