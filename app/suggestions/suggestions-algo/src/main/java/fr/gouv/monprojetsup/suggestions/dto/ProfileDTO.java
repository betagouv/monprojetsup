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
        @ArraySchema(arraySchema = @Schema(name = "geo_pref", description = "villes préférées pour étudier (code insee ou nom)", example = "[\"33514\",\"Nantes\"]"))
        Set<String> geo_pref,
        @ArraySchema(arraySchema = @Schema(name = "spe_classes", description = "enseignements de spécialité de terminale choisis ou envisagés", example = "[\"Sciences de la vie et de la Terre\",\"Mathématiques\"]"))
        Set<String> spe_classes,
        @ArraySchema(arraySchema = @Schema(name = "interests", description = "domaines et intérêts", example = "[\"ci1\",\"ci2\",\"ci3\",\"dom1\", \"dom2\", \"dom3\"]"))
        List<String> interests,
        @Schema(description = "moyenne générale scolaire estimée en terminale, sur 40", example = "28")
        String moygen,
        @ArraySchema(arraySchema =  @Schema(name = "choix", description = "sélection de formations, voeux, métiers et secteurs d'activité"))
        List<ChoiceDTO> choix,
        @Schema(description = "statut de réflexion 0/1/2", example = "0")
        String statut

) {

    public List<ChoiceDTO> suggApproved() {
        return choix == null ? List.of() : choix.stream().filter(s -> Objects.equals(s.status(), ChoiceDTO.SUGG_APPROVED)).toList();
    }

    public List<ChoiceDTO> suggRejected() {
        return choix == null ? List.of() : choix.stream().filter(s -> Objects.equals(s.status(), ChoiceDTO.SUGG_REJECTED)).toList();
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
