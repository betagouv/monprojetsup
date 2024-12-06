package fr.gouv.monprojetsup.data.model.psup;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

record FormationsSimilaires(
        //indexé par "fl1234""
        Map<String, FormationsSimilairesParBac> parFiliereOrigine
) {
    public FormationsSimilaires() {
        this(new HashMap<>());
    }

    public void add(String gFlCodOri, String gFlCodSim, int gFsSco, int iTcCod) {
        parFiliereOrigine.computeIfAbsent(gFlCodOri, z -> new FormationsSimilairesParBac())
                .parBac().computeIfAbsent(iTcCod, z -> new HashMap<>())
                .put(gFlCodSim, Long.valueOf(gFsSco));
    }

    public void normalize() {
        parFiliereOrigine.values().forEach(FormationsSimilairesParBac::normalize);
        //some cleanup, may improve performances?
        parFiliereOrigine.entrySet().removeIf(e -> e.getValue().parBac().isEmpty());
    }

    public @NotNull Map<Integer, @NotNull Map<String, @NotNull Long>> getStats(Set<String> psupKeys) {
        Map<Integer, @NotNull Map<String, @NotNull Long>> result = new HashMap<>();
        psupKeys.forEach(psupKey -> {
            FormationsSimilairesParBac stats = parFiliereOrigine.get(psupKey);
            if (stats != null) {
                stats.parBac().forEach(
                        (bac, sim) -> sim.forEach(
                                (key, value) -> result.computeIfAbsent(bac, z -> new HashMap<>()).merge(key, value, Long::sum)));
            }
        });
        return result;
    }


}
