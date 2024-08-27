package fr.gouv.monprojetsup.data.etl.loaders;

import fr.gouv.monprojetsup.data.domain.model.onisep.ThematiquesOnisep;
import fr.gouv.monprojetsup.data.domain.model.thematiques.Thematiques;
import fr.gouv.monprojetsup.data.etl.sources.DataSources;

import java.io.IOException;

public class ThematiquesLoader {
    public static Thematiques loadThematiques(DataSources sources) throws IOException {
        ThematiquesOnisep thematiquesOnisep = ThematiquesOnisepLoader.loadThematiquesOnisep(sources);
        return new Thematiques(thematiquesOnisep);
    }
}
