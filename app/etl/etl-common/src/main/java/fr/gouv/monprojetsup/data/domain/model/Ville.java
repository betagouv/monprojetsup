package fr.gouv.monprojetsup.data.domain.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Ville(
        @NotNull String id,
        @NotNull List<@NotNull LatLng> coords
) {

}