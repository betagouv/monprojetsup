package fr.gouv.monprojetsup.suggestions.algo;

import java.util.Map;

public record NaiveBayesScore(
    String key,
    Map<String,Double> scores
) {
}

