package fr.gouv.monprojetsup.data.model;

import fr.gouv.monprojetsup.data.model.stats.Statistique;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static fr.gouv.monprojetsup.data.model.stats.PsupStatistiques.TOUS_BACS_CODE_MPS;

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

    public boolean inclutTousBacs() {
        return
                admissions.isEmpty() || admissions.containsKey(TOUS_BACS_CODE_MPS)
                && nbAdmisParBac.isEmpty() || nbAdmisParBac.containsKey(TOUS_BACS_CODE_MPS)
                && pctAdmisParBac.isEmpty() || pctAdmisParBac.containsKey(TOUS_BACS_CODE_MPS);
    }

    public void restrictToBacs(@NotNull List<String> bacsKeys) {
        admissions.keySet().retainAll(bacsKeys);
        nbAdmisParBac.keySet().retainAll(bacsKeys);
        pctAdmisParBac.keySet().retainAll(bacsKeys);
    }

    public boolean hasStatsAdmissions() {
        return !admissions.isEmpty() || !nbAdmisParBac.isEmpty() || !pctAdmisParBac.isEmpty()
                || !nbAdmisParSpecialite.isEmpty() || !pctAdmisParSpecialite.isEmpty();
    }

    public boolean hasFullStats() {
        return !admissions.isEmpty() && !nbAdmisParBac.isEmpty() && !pctAdmisParBac.isEmpty()
                && !nbAdmisParSpecialite.isEmpty() && !pctAdmisParSpecialite.isEmpty();
    }
}
