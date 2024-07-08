package fr.gouv.monprojetsup.data.dto;

import fr.gouv.monprojetsup.data.dto.ProfileDTO;
import fr.gouv.monprojetsup.data.model.Explanation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetExplanationsAndExamplesServiceDTO {
    public record Request(
            @NotNull ProfileDTO profile,
            @ArraySchema(arraySchema = @Schema(description = "clés des formations et métiers pour lesquelles les explications sont demandées", example = "[\"fl2014\",\"fl2015\"]"))
            @NotNull List<String> keys
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
            @ArraySchema(arraySchema = @Schema(description = "explications", allOf = Explanation.class))
            @NotNull List<Explanation> explanations,
            @ArraySchema(arraySchema = @Schema(description = "examples de métiers, triés par affinité décroissante",
                    example = "[\"met_129\",\"met_84\",\"met_5\"]"))
            @NotNull List<String> examples
    ) {
    }
}
