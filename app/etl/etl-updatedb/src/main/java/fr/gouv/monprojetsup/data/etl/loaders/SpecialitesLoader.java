package fr.gouv.monprojetsup.data.etl.loaders;

import fr.gouv.monprojetsup.data.domain.model.specialites.Specialites;
import fr.gouv.monprojetsup.data.domain.model.stats.PsupStatistiques;

import java.io.IOException;

import static fr.gouv.monprojetsup.data.tools.Serialisation.fromJsonFile;

public class SpecialitesLoader {

    public static Specialites load(PsupStatistiques statistiques, DataSources sources) throws IOException {

        Specialites specsWithoutBacInfo =
            fromJsonFile(sources.getSourceDataFilePath(DataSources.SPECIALITES_FILENAME), Specialites.class);

        return Specialites.build(
                specsWithoutBacInfo,
                statistiques.getAdmisMatiereBacAnneeStats()
        );
    }

    private SpecialitesLoader() {

    }
}
