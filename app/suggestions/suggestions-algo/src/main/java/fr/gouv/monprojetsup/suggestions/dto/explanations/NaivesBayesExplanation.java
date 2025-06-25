package fr.gouv.monprojetsup.suggestions.dto.explanations;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/*
"popularity": 3.6699821356981728,
          "scores": [
            {
              "key": "Générale",
              "log_influence": 0.9176905251227548,
              "categorie": "id_baccalaureat"
            },
            {
              "key": "mat5",
              "log_influence": -3.273289227406298,
              "categorie": "specialites"
            },
*/
public record NaivesBayesExplanation(
        @JsonProperty(value = "popularity")
        double popularity,
        @JsonProperty(value = "scores")
        @NotNull List<NaiveBayesExplanationDetail> scores
) {
    public String toDebugString(
            String source,
            Map<String, @NotNull String> debugLabels
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("naive-bayes ");
        sb.append(source);
        sb.append(" : ");
        scores.forEach(d -> sb
                .append(debugLabels.getOrDefault(d.key(),d.key())).append(" ")
                .append("(influence ")
                .append(d.logInfluence()).append(" cat ")
                .append(d.categorie())
                .append(") ")
        );
        return sb.toString();
    }

    /*class ExplanationResponseScore(BaseModel):
    key: str = Field(
        description="Identifiant de la feature responsable de la différence de score"
    )
    log_influence: float = Field(
        description="Influence de cette feature sur le score, en échelle logarithmique"
    )
    categorie: str = Field(
        description="Catégorie dans laquelle se trouvait la feature.",
        examples=["corbeille_formation", "voeux_favoris"],
    )
*/

    record NaiveBayesExplanationDetail(
            @JsonProperty(value = "key")
            @NotNull String key,//Identifiant de la feature responsable de la différence de score
            @JsonProperty(value = "log_influence")
            float logInfluence,//Influence de cette feature sur le score, en échelle logarithmique
            @JsonProperty(value = "categorie")
            @NotNull String categorie//Catégorie dans laquelle se trouvait la feature
    ) {
    }
}
