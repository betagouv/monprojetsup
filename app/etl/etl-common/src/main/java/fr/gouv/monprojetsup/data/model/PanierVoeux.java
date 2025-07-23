package fr.gouv.monprojetsup.data.model;

import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.model.psup.LettreMotivation;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public record PanierVoeux(
        @NotNull String bac,
        @NotNull List<@NotNull String> voeux,
        @NotNull List<@NotNull LettreMotivation> lettres
) implements Serializable {
    public PanierVoeux(@NotNull String bac, @NotNull Collection<Integer> voeux, @NotNull List<@NotNull LettreMotivation> lettres) {
        this(bac, voeux.stream().map(Constants::gTaCodToMpsId).toList(), lettres);
    }
}
