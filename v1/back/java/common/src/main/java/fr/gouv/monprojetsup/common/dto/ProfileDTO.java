package fr.gouv.monprojetsup.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProfileDTO(

        @Schema(name = "niveau", description = "classe actuelle", example = "term", required = false, allowableValues = {"", "sec", "secSTHR", "secTMD", "prem", "term"})
        String niveau,
        @Schema(name = "bac", description = "type de Bac choisi ou envisagé", example = "Générale", required = false, allowableValues = {"", "Générale", "P", "PA", "S2TMD", "ST2S", "STAV", "STD2A", "STHR", "STI2D", "STL", "STMG"})
        String bac,
        @Schema(name = "duree", description = "durée envisagée des études", example = "long", required = false, allowableValues = {"", "court", "long", "indiff"})
        String duree,
        @Schema(name = "apprentissage", description = "intérêt pour les formations en apprentissage", example = "C", required = false, allowableValues = {"", "A", "B", "C", "D"})
        String apprentissage,
        @Schema(name = "geo_pref", description = "villes préférées pour étudier", example = "Soulac", required = false)
        Set<String> geo_pref,
        @Schema(name = "spe_classes", description = "enseignements de spécialité de terminale choisis ou envisagés", required = false)
        Set<String> spe_classes,
        @Schema(name = "scores", description = "centres d'intérêts", required = false)
        Map<String, Integer> scores,
        @Schema(name = "moygen", description = "moyenne générale scolaire estimée en terminale", example = "14", required = false)
        String moygen,
        @Schema(name = "choices", description = "sélection de formations, métiers et secteurs d'activité", required = false)
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
