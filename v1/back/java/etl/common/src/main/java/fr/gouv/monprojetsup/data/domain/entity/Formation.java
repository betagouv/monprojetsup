package fr.gouv.monprojetsup.data.domain.entity;

import fr.gouv.monprojetsup.data.model.cities.Coords;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public record Formation(

        @NotNull String id,
        @NotNull String label,
        int capacite,
        boolean apprentissage,
        int duree,
        @Nullable String las,
        @NotNull List<Voeu> voeux,
        @NotNull List<String> metiers,

        @NotNull StatsFormation stats
) {

    public List<String> getVoeuxIds() {
        return voeux.stream().map(Voeu::id).toList();
    }

    public List<Pair<String, Coords>> getVoeuxCoords() {
        return voeux.stream().map(v -> Pair.of(v.id(), Coords.of(v.lat(), v.lng()))).toList();
    }

    public int nbVoeux() {
        return voeux.size();
    }
}

