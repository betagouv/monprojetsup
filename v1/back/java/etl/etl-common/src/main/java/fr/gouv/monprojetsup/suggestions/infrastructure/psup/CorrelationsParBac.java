package fr.gouv.monprojetsup.suggestions.infrastructure.psup;

import java.util.HashMap;
import java.util.Map;

public record CorrelationsParBac(Map<Integer, Correlations> parBac) {

    public CorrelationsParBac() {
        this(new HashMap<>());
    }
}
