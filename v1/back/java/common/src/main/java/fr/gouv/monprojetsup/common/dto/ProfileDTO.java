package fr.gouv.monprojetsup.common.dto;

import java.util.*;
import java.util.stream.Collectors;

public record ProfileDTO(
        String niveau,
        String bac,
        String duree,
        String apprentissage,
        Set<String> geo_pref,
        Set<String> spe_classes,
        Map<String, Integer> scores,
        String mention,
        String moygen,
        Map<String, SuggestionDTO> choices
) {

    /*
    public ProfileDTO(fr.gouv.monprojetsup.app.dto.ProfileDTO dto) {
        this(
                dto.niveau(),
                dto.bac(),
                dto.duree(),
                dto.apprentissage(),
                dto.geo_pref(),
                dto.spe_classes(),
                dto.scores(),
                dto.mention(),
                dto.moygen(),
                dto.choices().entrySet().stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new Suggestion(e.getValue().fl())
                ))
        );
    }*/

    public List<SuggestionDTO> suggApproved() {
        return choices == null ? List.of() : choices.values().stream().filter(s -> s.status().equals(SuggestionDTO.SUGG_APPROVED)).toList();
    }

    public List<SuggestionDTO> suggRejected() {
        return choices == null ? List.of() : choices.values().stream().filter(s -> s.status().equals(SuggestionDTO.SUGG_REJECTED)).toList();
    }

    public int bacIndex() {
        if (bac == null) return 0;
        return switch (bac) {
            case "gen" -> 1;
            case "tec" -> 2;
            case "pro" -> 3;
            default -> 0;
        };
    }


    public ProfileDTO cleanupDates() {
        return new ProfileDTO(
                niveau,
                bac,
                duree,
                apprentissage,
                geo_pref,
                spe_classes,
                scores,
                mention,
                moygen,
                cleanupDates(choices)
        );
    }

    private Map<String, SuggestionDTO> cleanupDates(Map<String, SuggestionDTO> choices) {
        return choices.entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                e -> e.getKey(),
                                e -> e.getValue().anonymize()
                        )
                );
    }

}
