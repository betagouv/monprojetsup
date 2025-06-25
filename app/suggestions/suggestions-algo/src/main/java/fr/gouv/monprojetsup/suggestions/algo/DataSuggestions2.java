package fr.gouv.monprojetsup.suggestions.algo;

import fr.gouv.monprojetsup.suggestions.dto.suggestions2.Suggestions2SuggestionsDto;
import fr.gouv.monprojetsup.suggestions.dto.suggestions2.NaiveBayesExplanations;
import fr.gouv.monprojetsup.suggestions.dto.suggestions2.Suggestions2ExplanationsDto;
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
        Suggestions2ExplanationsDto explanations
) {

    public DataSuggestions2(String fl) {
        this(fl, Map.of(), null);
    }

    public static Map<String, DataSuggestions2> build(
            @NotNull List<String> keys,
            @NotNull NaiveBayesExplanations explanationsNaiveBayes
    ) {
        val explanationsNaiveBayesMap = explanationsNaiveBayes.getExplanations().stream().collect(
                Collectors.toMap(Suggestions2ExplanationsDto::getKey, e -> e)
        );
        val affinitiesMap = explanationsNaiveBayes.getAffinities().stream().collect(
                Collectors.toMap(Suggestions2SuggestionsDto::getKey, e -> e)
        );
        return explanationsNaiveBayes.getAffinities().stream()
                .filter(sugg -> keys.contains(sugg.getKey()))
                .collect(Collectors.toMap(
                Suggestions2SuggestionsDto::getKey,
                sugg -> {
                    val key = sugg.getKey();
                    val affinity = affinitiesMap.get(key);
                    val expl = explanationsNaiveBayesMap.get(key);
                    if(affinity != null && expl != null) {
                        return new DataSuggestions2(key, affinity.getScores(), expl);
                    } else if(affinity != null) {
                        return new DataSuggestions2(key, affinity.getScores(), new Suggestions2ExplanationsDto(key));
                    } else {
                        return new DataSuggestions2(key, Map.of(), new Suggestions2ExplanationsDto(key));
                    }
                })
        );
    }
}
