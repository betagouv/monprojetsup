package fr.gouv.monprojetsup.suggestions.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.domain.Helpers.isFiliere;


@JsonIgnoreProperties(ignoreUnknown = true)
public record ProfileDTO(

        @Schema(name = "niveau", description = "classe actuelle", example = "term", allowableValues = {"", "sec", "secSTHR", "secTMD", "prem", "term"})
        String niveau,
        @Schema(name = "bac", description = "type de Bac choisi ou envisagé", example = "Générale", allowableValues = {"", "Générale", "P", "PA", "S2TMD", "ST2S", "STAV", "STD2A", "STHR", "STI2D", "STL", "STMG"})
        String bac,
        @Schema(name = "duree", description = "durée envisagée des études", example = "long", allowableValues = {"", "court", "long", "indiff"})
        String duree,
        @Schema(name = "apprentissage", description = "intérêt pour les formations en apprentissage", example = "C", allowableValues = {"", "A", "B", "C", "D"})
        String apprentissage,
        @ArraySchema(arraySchema = @Schema(name = "geo_pref", description = "villes préférées pour étudier", example = "[\"Soulac-sur-Mer\",\"Nantes\"]"))
        Set<String> geo_pref,
        @ArraySchema(arraySchema = @Schema(name = "spe_classes", description = "enseignements de spécialité de terminale choisis ou envisagés", example = "[\"Sciences de la vie et de la Terre\",\"Mathématiques\"]"))
        Set<String> spe_classes,
        @ArraySchema(arraySchema = @Schema(name = "interests", description = "centres d'intérêt", example = "[\"T_ITM_1054\",\"T_ITM_1534\",\"T_ITM_1248\",\"T_ITM_1351\", \"T_ROME_2092381917\", \"T_IDEO2_4812\"]"))
        List<String> interests,
        @Schema(description = "moyenne générale scolaire estimée en terminale", example = "14")
        String moygen,
        @ArraySchema(arraySchema =  @Schema(name = "choices", description = "sélection de formations, métiers et secteurs d'activité"))
        List<SuggestionDTO> choices,
        @Schema(description = "statut de réflexion 0/1/2", example = "0")
        String statut

) {

    public List<SuggestionDTO> suggApproved() {
        return choices == null ? List.of() : choices.stream().filter(s -> s.status().equals(SuggestionDTO.SUGG_APPROVED)).toList();
    }

    public List<SuggestionDTO> suggRejected() {
        return choices == null ? List.of() : choices.stream().filter(s -> s.status().equals(SuggestionDTO.SUGG_REJECTED)).toList();
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


    private List<SuggestionDTO> cleanupDates(List<SuggestionDTO> choices) {
        return choices.stream().map(e -> e.anonymize()).toList();
    }

    public boolean isFavori(String key) {
        if (choices == null) return false;
        return choices.stream().anyMatch(s -> s.fl().equals(key) && s.status().equals(SuggestionDTO.SUGG_APPROVED));
    }

    public Set<String> bin() {
        if(choices == null) return Set.of();
        return choices.stream().filter(s -> s.status().equals(SuggestionDTO.SUGG_REJECTED)).map(SuggestionDTO::fl).collect(Collectors.toSet());
    }

    public void removeAllFormationChoices() {
          choices.removeIf(s -> isFiliere(s.fl()));
    }
}
