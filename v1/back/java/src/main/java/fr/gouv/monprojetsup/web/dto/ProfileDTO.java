package fr.gouv.monprojetsup.web.dto;

import fr.gouv.monprojetsup.suggestions.dto.SuggestionDTO;
import fr.gouv.monprojetsup.tools.Sanitizer;

import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.suggestions.algos.Suggestion.SUGG_APPROVED;

public record ProfileDTO(
        String login,
        String nom,
     String prenom,
     String niveau,
     String ine,
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

    /**
     * Return all groups ok, including fl and fr
     *
     * @return all groups ok, including fl and fr
     */
    public Collection<String> favoris() {
        return choices.values().stream()
                .filter(s -> Objects.equals(s.status(), SUGG_APPROVED))
                .map(SuggestionDTO::fl)
                .collect(Collectors.toSet());
    }

    public ProfileDTO sanitize() {
        return new ProfileDTO(
                login.replace("&#64;","@"),
                Sanitizer.sanitize(nom),
                Sanitizer.sanitize(prenom),
                Sanitizer.sanitize(niveau),
                Sanitizer.sanitize(ine),
                Sanitizer.sanitize(bac),
                Sanitizer.sanitize(duree),
                Sanitizer.sanitize(apprentissage),
                geo_pref == null ? Collections.emptySet() : geo_pref.stream().map(Sanitizer::sanitize).collect(Collectors.toSet()),
                spe_classes  == null ? Collections.emptySet() : spe_classes.stream().map(Sanitizer::sanitize).collect(Collectors.toSet()),
                scores  == null ? Collections.emptyMap() : scores.entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                e -> Sanitizer.sanitize(e.getKey()),
                                Map.Entry::getValue
                        )),
                Sanitizer.sanitize(mention),
                Sanitizer.sanitize(moygen),
                choices == null ? Collections.emptyMap() : choices.entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                e -> Sanitizer.sanitize(e.getKey()),
                                e -> e.getValue().sanitize()
                        ))
        );
    }
}
