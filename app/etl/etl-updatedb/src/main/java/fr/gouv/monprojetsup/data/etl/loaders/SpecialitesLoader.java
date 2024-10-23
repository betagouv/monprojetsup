package fr.gouv.monprojetsup.data.etl.loaders;

import fr.gouv.monprojetsup.data.model.psup.SpeBac;
import fr.gouv.monprojetsup.data.model.specialites.Specialites;
import fr.gouv.monprojetsup.data.model.stats.AdmisMatiereBacAnnee;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static fr.gouv.monprojetsup.data.tools.Serialisation.fromJsonFile;

public class SpecialitesLoader {

      public static @NotNull Specialites load(
              @NotNull List<@NotNull AdmisMatiereBacAnnee> admisMatieresBacsAnnee,
              @NotNull DataSources sources,
              @NotNull Collection<@NotNull SpeBac> spesBacs) throws IOException {

        Specialites specsFromFiles =
            fromJsonFile(sources.getSourceDataFilePath(DataSources.SPECIALITES_FILENAME), Specialites.class);

        return Specialites.build(
                specsFromFiles,
                admisMatieresBacsAnnee,
                spesBacs
        );
    }

    private SpecialitesLoader() {

    }
}
