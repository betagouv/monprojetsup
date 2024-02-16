package fr.gouv.monprojetsup.web.dto;

import fr.gouv.monprojetsup.suggestions.dto.SuggestionDTO;
import fr.gouv.monprojetsup.tools.Sanitizer;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public record ProfileUpdateDTO(
        String name,
        @Nullable String value,
        @Nullable String action,//"add"

        @Nullable List<SuggestionDTO> suggestions

) {

    public ProfileUpdateDTO sanitize() {

        return new ProfileUpdateDTO(
                Sanitizer.sanitize(name),
                Sanitizer.sanitize(value),
                Sanitizer.sanitize(action),
                suggestions == null ? Collections.emptyList() : suggestions.stream().map(SuggestionDTO::sanitize).toList()
        );
    }
}
