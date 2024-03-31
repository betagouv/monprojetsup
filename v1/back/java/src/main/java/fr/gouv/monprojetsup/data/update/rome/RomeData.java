package fr.gouv.monprojetsup.data.update.rome;

import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.tools.Serialisation;

import java.io.IOException;

public record RomeData(
        InteretsRome centresInterest,
        Themes themes
) {

    public static RomeData load() throws IOException {

        InteretsRome centres = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.ROME_CENTRES_INTERETS_PATH),
                InteretsRome.class
        );

        Themes themes = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.ROME_THEMES_PATH),
                Themes.class
        );

        return new RomeData(centres, themes);

    }

}
