package fr.gouv.monprojetsup.data.etl.loaders;

import fr.gouv.monprojetsup.data.model.rome.InteretsRome;
import fr.gouv.monprojetsup.data.model.rome.RomeData;
import fr.gouv.monprojetsup.data.model.rome.Themes;
import fr.gouv.monprojetsup.data.tools.Serialisation;

import java.io.IOException;

public class RomeDataLoader {
    public static RomeData load(DataSources sources) throws IOException {

        InteretsRome centres = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(DataSources.ROME_CENTRES_INTERETS_PATH),
                InteretsRome.class
        );

        Themes themes = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(DataSources.ROME_THEMES_PATH),
                Themes.class
        );

        return new RomeData(centres, themes);

    }
}
