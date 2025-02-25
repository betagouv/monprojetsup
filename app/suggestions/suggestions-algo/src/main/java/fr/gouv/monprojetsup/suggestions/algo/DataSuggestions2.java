package fr.gouv.monprojetsup.suggestions.algo;

import fr.gouv.monprojetsup.suggestions.dto.GetExplanationsAndExamplesServiceDTO.ExplanationAndExamples;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record DataSuggestions2(
        double affinity,
        @Nullable ExplanationAndExamples explanations
) {
    public static Map<String, DataSuggestions2> build(
            @NotNull List<ExplanationAndExamples> explanationsNaiveBayes
    ) {
        val explanationsNaiveBayesMap = explanationsNaiveBayes.stream().collect(
                Collectors.toMap(ExplanationAndExamples::key, e -> e)
        );
        return explanationsNaiveBayes.stream().collect(
                Collectors.toMap(
                        ExplanationAndExamples::key,
                        a -> new DataSuggestions2(
                                a.affinity(),
                                explanationsNaiveBayesMap.get(a.key())
                        )
                )
        );
    }
}
