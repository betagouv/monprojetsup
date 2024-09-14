package fr.gouv.monprojetsup.data.model.psup;

import java.util.HashMap;
import java.util.Map;

public record CorrelationsParBac(Map<Integer, Correlations> parBac) {

    public CorrelationsParBac() {
        this(new HashMap<>());
    }
}
