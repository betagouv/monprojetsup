package fr.gouv.monprojetsup.data.domain.model;

import fr.gouv.monprojetsup.data.domain.model.stats.Statistique;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public record StatsFormation(

        //typeBac --> mid50
        @NotNull Map<@NotNull String, @NotNull Statistique> admissions,

        //typeBac --> nbAdmis et pct Admis
        @NotNull Map<@NotNull String, @NotNull Integer> nbAdmisParBac,
        @NotNull Map<@NotNull String, @NotNull Integer> pctAdmisParBac,

        //specialitesId --> nbAdmis et pct Admis
        @NotNull Map<@NotNull Integer, @NotNull Integer> nbAdmisParSpecialite,
        @NotNull Map<@NotNull Integer, @NotNull Integer> pctAdmisParSpecialite,

        //coarseTypeBac --> formationId --> score
        @NotNull Map<@NotNull Integer, @NotNull Map<@NotNull String, @NotNull Integer>> formationsSimilaires

) {

    public void restrictToBacs(@NotNull List<String> bacsKeys) {
        admissions.keySet().retainAll(bacsKeys);
        nbAdmisParBac.keySet().retainAll(bacsKeys);
        pctAdmisParBac.keySet().retainAll(bacsKeys);
    }
}
