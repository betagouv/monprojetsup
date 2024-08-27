package fr.gouv.monprojetsup.data.domain.model.psup;

import fr.parcoursup.carte.algos.tools.Paire;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record Correlations(List<CorrelationPaireFiliere> correlations) {

    @SuppressWarnings("unused")
    public Correlations() {
        this (new ArrayList<>());
    }

    public Correlations(Map<Paire<Integer, Integer>, Double> corrs) {
        this (new ArrayList<>());
        corrs.forEach((integerIntegerPaire, aDouble) -> correlations.add(new CorrelationPaireFiliere(integerIntegerPaire, aDouble)));

    }
}
