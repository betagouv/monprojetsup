package fr.gouv.monprojetsup.data.etl.loaders;

import fr.gouv.monprojetsup.data.model.specialites.Specialites;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;

import java.io.IOException;

import static fr.gouv.monprojetsup.data.tools.Serialisation.fromJsonFile;

public class SpecialitesLoader {

    public static Specialites load(PsupStatistiques statistiques, DataSources sources) throws IOException {

        Specialites specsFromFiles =
            fromJsonFile(sources.getSourceDataFilePath(DataSources.SPECIALITES_FILENAME), Specialites.class);

        return Specialites.build(
                specsFromFiles,
                statistiques.getAdmisMatiereBacAnneeStats()
        );
    }

    private SpecialitesLoader() {

    }
}
