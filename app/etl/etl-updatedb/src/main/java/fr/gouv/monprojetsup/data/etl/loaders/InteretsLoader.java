package fr.gouv.monprojetsup.data.etl.loaders;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class InteretsLoader {
    public static @NotNull List<@NotNull Map<String, @NotNull String>> loadInterets(@NotNull DataSources dataSources) {
        return CsvTools.readCSV(dataSources.getSourceDataFilePath(DataSources.INTERETS_GROUPES_PATH), '\t');
    }
}
