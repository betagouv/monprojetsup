package fr.gouv.monprojetsup.suggestions.dto.explanations;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/*class NaivesBayesExplanationDetail(BaseModel):
    item_key: str = Field(description="clé de l'item", examples=["mat5", "ci1"])
    score: float = Field(
        description="score mesurant l'impact de l'item la sélection de la formation",
        examples=[-0.12345, 1.78253],
    )
    side: Literal["positive", "negative"] = Field(
        description="si l'item était présent positivement ou négativement dans le profil."
    )
*/
public record NaivesBayesExplanation(
        @JsonProperty(value = "details")
        @NotNull List<NaiveBayesExplanationDetail> details
) {
    public String toDebugString(Map<String, @NotNull String> debugLabels) {
        StringBuilder sb = new StringBuilder();
        sb.append("naive-bayes: ");
        details.forEach(d -> sb
                .append(debugLabels.getOrDefault(d.itemKey(),d.itemKey())).append(" ")
                .append("(")
                .append(d.affinity()).append(" ")
                .append(d.side(), 0, 3)
                .append(") ")
        );
        return sb.toString();
    }


    record NaiveBayesExplanationDetail(
            @JsonProperty(value = "item_key")
            @NotNull String itemKey,
            @JsonProperty(value = "score")
            float affinity,
            @JsonProperty(value = "side")
            @NotNull String side
    ) { }
}
