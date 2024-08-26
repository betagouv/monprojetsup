package fr.gouv.monprojetsup.data.domain.model;

import fr.gouv.monprojetsup.data.domain.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public record Candidat(
        List<@NotNull String> voeux
) {
    public Candidat(@NotNull Set<Integer> voeux) {
        this(voeux.stream().map(Constants::gTaCodToFrontId).toList());
    }
}
