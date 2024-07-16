package fr.gouv.monprojetsup.suggestions.data.update.psup;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record FormationsSimilaires(
        //indexé par "fl1234""
        Map<String, FormationsSimilairesParBac> parFiliereOrigine
) {
    public FormationsSimilaires() {
        this(new HashMap<>());
    }

    public Map<String, Integer> get(String fl, int bacIndex) {
        FormationsSimilairesParBac t = parFiliereOrigine().get(fl);
        //TODO remove and use new format for sim
        if (t == null) return Collections.emptyMap();
        Map<String, Integer> sim = t.parBac().get(bacIndex);
        if (sim == null) sim = t.parBac().get(0);
        return sim;
    }

    public void add(String gFlCodOri, String gFlCodSim, int gFsSco, int iTcCod) {
        parFiliereOrigine.computeIfAbsent(gFlCodOri, z -> new FormationsSimilairesParBac())
                .parBac().computeIfAbsent(iTcCod, z -> new HashMap<>())
                .put(gFlCodSim, gFsSco);
    }

    public void normalize() {
        parFiliereOrigine.values().forEach(FormationsSimilairesParBac::normalize);
        //some cleanup, may improve performances?
        parFiliereOrigine.entrySet().removeIf(e -> e.getValue().parBac().isEmpty());
    }
}
