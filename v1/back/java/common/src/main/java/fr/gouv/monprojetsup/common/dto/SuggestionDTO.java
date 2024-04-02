package fr.gouv.monprojetsup.common.dto;

import fr.gouv.monprojetsup.common.Sanitizer;
import org.jetbrains.annotations.Nullable;

public record SuggestionDTO(
            String fl,
            @Nullable Integer status,
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