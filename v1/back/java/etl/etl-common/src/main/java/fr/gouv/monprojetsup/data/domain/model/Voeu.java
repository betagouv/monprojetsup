package fr.gouv.monprojetsup.data.domain.model;

import fr.gouv.monprojetsup.data.infrastructure.psup.DescriptifVoeu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Voeu(
        @NotNull String id,
        @NotNull String formation,
        @Nullable Double lat,
        @Nullable Double lng,
        @NotNull String libelle,
        int capacite,
        @Nullable DescriptifVoeu descriptif,
        @NotNull String commune,
        @NotNull String codeCommune
        ) {
}
