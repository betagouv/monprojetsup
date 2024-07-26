package fr.gouv.monprojetsup.suggestions.domain.entity;

import fr.gouv.monprojetsup.suggestions.data.model.descriptifs.Descriptifs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Voeu(
        @NotNull String id,
        @NotNull String formation,
        @Nullable Double lat,
        @Nullable Double lng,
        @NotNull String libelle,
        int capacite,
        @Nullable Descriptifs.Descriptif descriptif
) {
}
