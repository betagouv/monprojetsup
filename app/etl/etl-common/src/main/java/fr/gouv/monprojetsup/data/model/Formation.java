package fr.gouv.monprojetsup.data.model;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public record Formation(

        @NotNull String id,
        @NotNull String typeFormation,
        @NotNull String label,
        @Nullable String labelDebug,
        int capacite,
        boolean apprentissage,
        int duree,
        @Nullable String las,
        @NotNull List<@NotNull Voeu> voeux,
        @NotNull StatsFormation stats,
        @NotNull List<@NotNull String> filieresPsup

        ) {

    public List<Pair<String, LatLng>> getVoeuxCoords() {
        return voeux.stream().map(v -> Pair.of(v.id(), v.coords())).toList();
    }

    public int nbVoeux() {
        return voeux.size();
    }

}

