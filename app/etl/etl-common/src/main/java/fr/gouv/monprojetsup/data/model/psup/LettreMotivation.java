package fr.gouv.monprojetsup.data.model.psup;

import fr.gouv.monprojetsup.data.Constants;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public record LettreMotivation(
        @NotNull String lettre,
        @NotNull List<@NotNull String> voeux
        ) implements Serializable {

    public LettreMotivation(@NotNull String lettre, @NotNull Set<Integer> voeux) {
        this(lettre, voeux.stream().map(Constants::gTaCodToMpsId).toList());
    }
}
