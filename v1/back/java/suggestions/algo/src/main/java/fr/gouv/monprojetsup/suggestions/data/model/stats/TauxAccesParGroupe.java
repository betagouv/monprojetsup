package fr.gouv.monprojetsup.suggestions.data.model.stats;

import java.util.Map;
import java.util.TreeMap;

import static fr.gouv.monprojetsup.suggestions.data.Constants.FORMATION_PREFIX;

public record TauxAccesParGroupe(
        Map<String, TauxAccesParBac> parGroupe
) {
    public TauxAccesParGroupe() {
        this(new TreeMap<>());
    }

    public void add(Map<Integer, Map<Integer, Integer>> tauxAcces) {
        tauxAcces.forEach((gTaCod, m) -> parGroupe.computeIfAbsent(FORMATION_PREFIX + gTaCod, z -> new TauxAccesParBac()).parBac().putAll(m));
    }
}
