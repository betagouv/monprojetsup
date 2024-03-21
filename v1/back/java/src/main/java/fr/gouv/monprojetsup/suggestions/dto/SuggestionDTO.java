package fr.gouv.monprojetsup.suggestions.dto;

import fr.gouv.monprojetsup.tools.Sanitizer;
import org.jetbrains.annotations.Nullable;

public record SuggestionDTO(
            String fl,
            @Nullable Integer status,
            @Nullable String date
    ) {

    public SuggestionDTO sanitize() {
        return new SuggestionDTO(
                Sanitizer.sanitize(fl),
                status,
                Sanitizer.sanitize(date)
        );
    }
}