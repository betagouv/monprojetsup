package fr.gouv.monprojetsup.data.model;

import fr.gouv.monprojetsup.data.Constants;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public record PanierVoeux(
        @NotNull String bac,
        @NotNull List<@NotNull String> voeux
) implements Serializable {
    public PanierVoeux(@NotNull String bac, @NotNull Set<Integer> voeux) {
        this(bac, voeux.stream().map(Constants::gTaCodToMpsId).toList());
    }
}
