package fr.gouv.monprojetsup.suggestions.port;

import fr.gouv.monprojetsup.data.model.Ville;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface VillesPort {

    @Nullable Ville getVille(@NotNull String id);

}
