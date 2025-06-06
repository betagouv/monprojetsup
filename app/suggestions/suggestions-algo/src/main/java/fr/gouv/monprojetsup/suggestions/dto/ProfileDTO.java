package fr.gouv.monprojetsup.suggestions.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static fr.gouv.monprojetsup.data.Constants.isFiliere;


@JsonIgnoreProperties(ignoreUnknown = true)
public record ProfileDTO(

        @Schema(name = "niveau", description = "classe actuelle", example = "term", allowableValues = {"", "sec", "prem", "term"})
        String niveau,
        @Schema(name = "bac", description = "type de Bac choisi ou envisagé", example = "Générale", nullable = true, allowableValues = {"", "Générale", "P", "PA", "S2TMD", "ST2S", "STAV", "STD2A", "STHR", "STI2D", "STL", "STMG", "NC"})
        @Nullable String bac,
        @Schema(name = "duree", description = "durée envisagée des études", example = "long", allowableValues = {"", "court", "long", "indiff"})
        String duree,
        @Schema(name = "apprentissage", description = "intérêt pour les formations en apprentissage", example = "C", allowableValues = {"", "A", "B", "C", "D"})
        String apprentissage,
        @ArraySchema(arraySchema = @Schema(name = "geo_pref", description = "villes préférées pour étudier (code insee)", example = "[\"33514\",\"44001\"]"))
        Set<String> geo_pref,
        @ArraySchema(arraySchema = @Schema(name = "spe_classes", description = "spécialités (eds ou spécialités de bac) de terminale choisis ou envisagés", example = "[\"sp757\",\"mat5\"]"))
        Set<String> spe_classes,
        @ArraySchema(arraySchema = @Schema(name = "interests", description = "domaines et intérêts", example = "[\"ci1\",\"ci2\",\"ci3\",\"dom1\", \"dom2\", \"dom3\"]"))
        List<String> interests,
        @ArraySchema(arraySchema =  @Schema(name = "choix", description = "sélection de formations, voeux et métiers"))
        List<ChoiceDTO> choix,
        @Schema(description = "statut de réflexion", example = "quelques_pistes", allowableValues = { "aucune_idee", "quelques_pistes", "projet_precis" })
        String situation

) {

    public List<ChoiceDTO> suggApproved() {
        return choix == null ? List.of() : choix.stream().filter(ChoiceDTO::isApproved).toList();
    }

    public List<ChoiceDTO> suggRejected() {
        return choix == null ? List.of() : choix.stream().filter(ChoiceDTO::isRejected).toList();
    }

    public int bacIndex() {
        if(Objects.isNull(bac)) return 0;
        if(bac.startsWith("S")) return 2;
        return switch (bac) {
            case "Générale" -> 1;
            case "P", "PA" -> 3;
            default -> 0;
        };
    }


    public void removeAllFormationChoices() {
          choix.removeIf(s -> isFiliere(s.id()));
    }
}
