package fr.gouv.monprojetsup.common.dto;

import fr.gouv.monprojetsup.data.ServerData;

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


    public String toExplanationString() {
        return "Profil: \n" + toExplanationStringShort("\t") +
                "\n\tcentres d'intérêts: " + toExplanationString(scores, "\t") + "\n" +
                "\n\tformations et métiers d'intérêt: " + toExplanationString(suggApproved(), "\t") + "\n" +
                "\n\tcorbeille (refus / rejet): " + toExplanationString(suggRejected(), "\t") + "\n" +
                '}';
    }

    public String toExplanationStringShort(String sep) {
        return sep + "niveau: '" + niveau + "'\n" +
                sep + "bac: '" + bac + "'\n" +
                sep + "duree: '" + duree + "'\n" +
                sep + "apprentissage: '" + toApprentissageExplanationString(apprentissage) + "'\n" +
                sep + "geo_pref: " + geo_pref + "'\n" +
                sep + "spe_classes: " + spe_classes + "'\n" +
                sep + "moyenne générale auto-évaluée: '" + moygen + "'\n";
    }


    private String toApprentissageExplanationString(String apprentissage) {
        if (apprentissage == null) return "Non-renseigné";
        if (apprentissage.equals("A")) return "Indifférent";
        if (apprentissage.equals("B")) return "Indifférent";
        if (apprentissage.equals("C")) return "Peu intéressé";
        return apprentissage;
    }

    public static String toExplanationString(List<SuggestionDTO> suggestions, String sep) {
        if (suggestions == null) return sep;
        return suggestions.stream()
                .map(s -> ServerData.getDebugLabel(s.fl()))
                .reduce(sep + sep, (a, b) -> a + "\n" + sep + sep + b);
    }

    public static String toExplanationString(Map<String, Integer> scores, String sep) {
        return scores.entrySet().stream()
                .map(e -> ServerData.getDebugLabel(e.getKey()) + ":" + e.getValue())
                .reduce(sep + sep, (a, b) -> a + "\n" + sep + sep + b);
    }

    public static String toExplanations(List<String> list, String sep) {
        return list.stream()
                .map(e -> ServerData.getDebugLabel(e))
                .reduce(sep + sep, (a, b) -> a + "\n" + sep + sep + b);
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
