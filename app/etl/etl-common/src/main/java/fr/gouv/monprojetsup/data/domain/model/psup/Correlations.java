package fr.gouv.monprojetsup.data.domain.model.psup;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record Correlations(List<CorrelationPaireFiliere> correlations) {

    @SuppressWarnings("unused")
    public Correlations() {
        this (new ArrayList<>());
    }

    public Correlations(Map<Pair<Integer, Integer>, Double> corrs) {
        this (new ArrayList<>());
        corrs.forEach((integerIntegerPaire, aDouble) -> correlations.add(new CorrelationPaireFiliere(integerIntegerPaire, aDouble)));

    }
}
