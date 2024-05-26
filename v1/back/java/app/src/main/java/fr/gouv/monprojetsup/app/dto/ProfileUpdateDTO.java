package fr.gouv.monprojetsup.app.dto;

import fr.gouv.monprojetsup.common.Sanitizer;
import fr.gouv.monprojetsup.data.dto.SuggestionDTO;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
