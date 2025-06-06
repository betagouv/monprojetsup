package fr.gouv.monprojetsup.data.model;

import fr.gouv.monprojetsup.data.model.stats.Middle50;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record StatsFormation(

        //typeBac --> mid50
        @NotNull Map<@NotNull String, @NotNull Middle50> admissions,

        //typeBac --> nbAdmis et pct Admis
        @NotNull Map<@NotNull String, @NotNull Integer> nbAdmisParBac,

        @NotNull Map<@NotNull String, @NotNull Integer> pctAdmisParSpecialite,

        //coarseTypeBac --> formationId --> score
        @NotNull Map<@NotNull Integer, @NotNull Map<@NotNull String, @NotNull Long>> formationsSimilaires

) {

    public boolean hasStatsAdmissions() {
        return !admissions.isEmpty() || !nbAdmisParBac.isEmpty()
                || !pctAdmisParSpecialite.isEmpty();
    }

    public boolean hasFullStats() {
        return !admissions.isEmpty() && !nbAdmisParBac.isEmpty()
                && !pctAdmisParSpecialite.isEmpty();
    }
}
