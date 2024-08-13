package fr.gouv.monprojetsup.suggestions.domain.model;

import fr.gouv.monprojetsup.suggestions.infrastructure.model.stats.Statistique;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record StatsFormation(

        //typeBac --> mid50
        @NotNull Map<String, @NotNull Statistique> admissions,

        //typeBac --> nbAdmis et pct Admis
        @NotNull Map<String, @NotNull Integer> nbAdmisParBac,
        @NotNull Map<String, @NotNull Integer> pctAdmisParBac,

        //specialitesId --> nbAdmis et pct Admis
        @NotNull Map<Integer, @NotNull Integer> nbAdmisParSpecialite,
        @NotNull Map<Integer, @NotNull Integer> pctAdmisParSpecialite,

        //coarseTypeBac --> formationId --> score
        @NotNull Map<Integer, @NotNull Map<String, @NotNull Integer>> formationsSimilaires

) {

}
