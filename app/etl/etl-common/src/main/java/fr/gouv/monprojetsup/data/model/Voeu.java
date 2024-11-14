package fr.gouv.monprojetsup.data.model;

import fr.gouv.monprojetsup.data.model.psup.DescriptifVoeu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static fr.gouv.monprojetsup.data.Constants.FORMATION_PREFIX;
import static fr.gouv.monprojetsup.data.Constants.PSUP_FORMATION_FICHE;

public record Voeu(
        @NotNull String id,
        @Nullable Double lat,
        @Nullable Double lng,
        @NotNull String libelle,
        int capacite,
        @Nullable DescriptifVoeu descriptif,
        @NotNull String commune,
        @NotNull String codeCommune
        ) {

    public @Nullable LatLng coords() {
        if(lat == null || lng == null) {
            return null;
        }
        return new LatLng(lat, lng);
    }

    public @NotNull String getUrl() {
        if(!id.startsWith(FORMATION_PREFIX)) {
            throw new IllegalArgumentException("Invalid id: " + id);
        }
        return PSUP_FORMATION_FICHE + id.substring(2);
    }

}
