package fr.gouv.monprojetsup.suggestions.dto;

import fr.gouv.monprojetsup.suggestions.dto.explanations.Explanation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GetExplanationsAndExamplesServiceDTO {
    public record Request(

            @Schema(name = "profile", description = "Profil utilisé.")
            @NotNull ProfileDTO profile,

            @ArraySchema(arraySchema = @Schema(description = "clés des formations et métiers pour lesquelles les explications sont demandées", example = "[\"fl210\",\"fr22\",\"fl2014\"]"))
            @NotNull List<String> keys,

            @Schema(name = "inclureExplicationsDetaillees", description = "Inclure les explications detaillées des calculs des scores.", example = "false")
            @Nullable Boolean inclureExplicationsDetaillees

    ) {
    }

    public record Response(
            @NotNull ResponseHeader header,

            @ArraySchema(arraySchema = @Schema(description = "liste des résultats", allOf = ExplanationAndExamples.class))
            @NotNull List<ExplanationAndExamples> liste
    ) {
        public Response(
                List<ExplanationAndExamples> liste
        ) {
            this(new ResponseHeader(), liste);
        }

    }

    public record ExplanationAndExamples(
            @Schema(description = "clé", example = "fl2014")
            String key,
            @Schema(description = "affinite", example = "0.25")
            double affinity,
            @ArraySchema(arraySchema = @Schema(description = "explications", allOf = Explanation.class))
            @NotNull List<Explanation> explanations,
            @ArraySchema(arraySchema = @Schema(description = "exemples de métiers, triés par affinité décroissante",
                    example = "[\"MET.129\",\"MET.84\",\"MET.5\"]"))
            @Nullable List<String> metiers
    ) {
    }
}
