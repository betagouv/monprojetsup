package fr.gouv.monprojetsup.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.gouv.monprojetsup.common.Sanitizer;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SuggestionDTO(
            @Schema(name = "fl", example = "fl2014", description = "clé de la formation, du métier ou du secteur d'activité", required = true)
            @NotNull String fl,
            @Schema(name = "status", example = "1", description = "statut. \"1\": dans les favoris. \"2\": dans la corbeille.", allowableValues = {"0", "1", "2" }, required = true)
            @NotNull Integer status,
            @Nullable String date
    ) {

    public static final int SUGG_PENDING = 0;
    public static final int SUGG_APPROVED = 1;
    public static final int SUGG_REJECTED = 2;

    public SuggestionDTO sanitize() {
        return new SuggestionDTO(
                Sanitizer.sanitize(fl),
                status,
                Sanitizer.sanitize(date)
        );
    }

    public SuggestionDTO anonymize() {
            return new SuggestionDTO(
                    fl, status, null
            );
    }

    public SuggestionDTO updateStatus(Integer status) {
        return new SuggestionDTO(
                fl, status, date
        );
    }
}