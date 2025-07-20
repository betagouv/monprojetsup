package fr.gouv.monprojetsup.suggestions.algo;

import fr.gouv.monprojetsup.suggestions.dto.explanations.NaivesBayesExplanation;

import java.util.Map;

public record NaiveBayesExplanation (
    String key,
    Map<String, NaivesBayesExplanation> explanations
) {
    public NaiveBayesExplanation() {
        this("", Map.of());
    }
}
