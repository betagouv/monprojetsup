package fr.gouv.monprojetsup.data.domain.model.psup;

import java.util.HashMap;
import java.util.Map;

public record CorrelationsParBac(Map<Integer, Correlations> parBac) {

    public CorrelationsParBac() {
        this(new HashMap<>());
    }
}
