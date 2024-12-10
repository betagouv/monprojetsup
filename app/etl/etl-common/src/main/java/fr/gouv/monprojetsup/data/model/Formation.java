package fr.gouv.monprojetsup.data.model;

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
        @NotNull StatsFormation stats,
        @NotNull List<@NotNull String> filieresPsup

        ) {

}

