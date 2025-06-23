package fr.gouv.monprojetsup.suggestions.algo;

import fr.gouv.monprojetsup.suggestions.dto.GetExplanationsAndExamplesServiceDTO.ExplanationAndExamples;
import fr.gouv.monprojetsup.suggestions.dto.suggestions2.Suggestions2MultiExplanationsDto;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record DataSuggestions2(
        double affinity,
        @Nullable
        Suggestions2MultiExplanationsDto explanations
) {
    public static Map<String, DataSuggestions2> build(
            @NotNull List<String> keys,
            @NotNull List<Suggestions2MultiExplanationsDto> explanationsNaiveBayes
    ) {
        val explanationsNaiveBayesMap = explanationsNaiveBayes.stream().collect(
                Collectors.toMap(Suggestions2MultiExplanationsDto::getKey, e -> e)
        );
        val result = new HashMap<>(explanationsNaiveBayes.stream().collect(
                Collectors.toMap(
                        Suggestions2MultiExplanationsDto::getKey,
                        a -> new DataSuggestions2(
                                a.,
                                explanationsNaiveBayesMap.get(a.key())
                        )
                )
        ));
        for (String key : keys) {
            if (!result.containsKey(key)) {
                result.put(key, new DataSuggestions2(-1, null));
            }
        }
        return result;
    }
}
