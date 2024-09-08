package fr.gouv.monprojetsup.data.domain.model;

import fr.gouv.monprojetsup.data.domain.Constants;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public record Candidat(
        @NotNull String bac,
        @NotNull List<@NotNull String> voeux
) implements Serializable {
    public Candidat(@NotNull String bac, @NotNull Set<Integer> voeux) {
        this(bac, voeux.stream().map(Constants::gTaCodToMpsId).toList());
    }
}
