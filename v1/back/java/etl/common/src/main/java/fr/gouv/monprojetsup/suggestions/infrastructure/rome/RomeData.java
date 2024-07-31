package fr.gouv.monprojetsup.suggestions.infrastructure.rome;

import fr.gouv.monprojetsup.suggestions.infrastructure.DataSources;
import fr.gouv.monprojetsup.suggestions.tools.Serialisation;

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
