package fr.gouv.monprojetsup.data.infrastructure.rome;

import fr.gouv.monprojetsup.data.infrastructure.DataSources;
import fr.gouv.monprojetsup.data.tools.Serialisation;

import java.io.IOException;

public record RomeData(
        InteretsRome centresInterest,
        Themes themes
) {

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
