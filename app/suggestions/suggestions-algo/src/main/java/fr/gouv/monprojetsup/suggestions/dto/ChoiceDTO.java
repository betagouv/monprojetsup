package fr.gouv.monprojetsup.suggestions.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChoiceDTO(
            @Schema(example = "fl2014", description = "clé de la formation, du métier ou du secteur d'activité")
            @NotNull String id,
            @Schema(example = "1", description = "statut. \"1\": dans les favoris. \"2\": dans la corbeille.", allowableValues = {"0", "1", "2" })
            @Nullable Integer status,

            @Schema(example = "1", description = "score. Entre 1 (bof) et 5 (génial).", allowableValues = {"0", "1", "2", "3", "4", "5"} )
            @Nullable Integer score
    ) {

    public static final int SUGG_PENDING = 0;
    public static final int SUGG_APPROVED = 1;
    public static final int SUGG_REJECTED = 2;


    public boolean isApproved() {
        return Objects.equals(status, SUGG_APPROVED);
    }

    public boolean isRejected() {
        return Objects.equals(status, SUGG_REJECTED);
    }
}