package fr.gouv.monprojetsup.suggestions.algo;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record DataSuggestions2(
        @NotNull String key,//fl12
        @NotNull Map<String,Double> scores,/// indexed e.g. by "expert" or "eleve"
        @Nullable
        NaiveBayesExplanation explanations
) {

    public DataSuggestions2(String fl) {
        this(fl, Map.of(), null);
    }

    public static Map<String, DataSuggestions2> build(
            @NotNull List<String> keys,
            @NotNull NaiveBayesExplanations explanationsNaiveBayes
    ) {
        val explanationsNaiveBayesMap = explanationsNaiveBayes.explanations().stream().collect(
                Collectors.toMap(NaiveBayesExplanation::key, e -> e)
        );
        val affinitiesMap = explanationsNaiveBayes.affinities().stream().collect(
                Collectors.toMap(NaiveBayesScore::key, e -> e)
        );
        return explanationsNaiveBayes.affinities().stream()
                .filter(sugg -> keys.contains(sugg.key()))
                .collect(Collectors.toMap(
                NaiveBayesScore::key,
                sugg -> {
                    val key = sugg.key();
                    val affinity = affinitiesMap.get(key);
                    val expl = explanationsNaiveBayesMap.get(key);
                    if(affinity != null && expl != null) {
                        return new DataSuggestions2(key, affinity.scores(), expl);
                    } else if(affinity != null) {
                        return new DataSuggestions2(key, affinity.scores(), new NaiveBayesExplanation(key, Map.of()));
                    } else {
                        return new DataSuggestions2(key, Map.of(), new NaiveBayesExplanation(key, Map.of()));
                    }
                })
        );
    }
}
