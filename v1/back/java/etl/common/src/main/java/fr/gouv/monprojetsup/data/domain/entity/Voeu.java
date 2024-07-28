package fr.gouv.monprojetsup.data.domain.entity;

import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs;
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
