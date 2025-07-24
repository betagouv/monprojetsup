package fr.gouv.monprojetsup.suggestions.algo;

import java.util.List;

public record NaiveBayesExplanations(
        List<NaiveBayesScore> affinities ,
        List<NaiveBayesExplanation> explanations
) {

    public NaiveBayesExplanations() {
        this(List.of(), List.of());
    }
}

