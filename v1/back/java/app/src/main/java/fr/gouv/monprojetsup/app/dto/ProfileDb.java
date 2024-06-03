package fr.gouv.monprojetsup.app.dto;

import fr.gouv.monprojetsup.app.db.model.Profile;
import fr.gouv.monprojetsup.common.Sanitizer;
import fr.gouv.monprojetsup.data.dto.ProfileDTO;
import fr.gouv.monprojetsup.data.dto.SuggestionDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.dto.SuggestionDTO.SUGG_APPROVED;


public record ProfileDb(
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
     Map<String, SuggestionDTO> choices,
        String statut,
        List<Retour> retours
) {


    public record Retour(String author, String type, String key, String content, String date) {
        public Retour sanitize() {
            return new Retour(
                    Sanitizer.sanitize(author),
                    Sanitizer.sanitize(type),
                    Sanitizer.sanitize(key),
                    Sanitizer.sanitize(content),
                    date == null ? LocalDate.now().toString() : Sanitizer.sanitize(date)
            );
        }
    }

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

    public ProfileDb sanitize() {
        return new ProfileDb(
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
                        )),
                Sanitizer.sanitize(statut),
                 new ArrayList<>(retours == null ? Collections.emptyList() : retours.stream()
                        .map(Retour::sanitize)
                        .toList())
        );
    }

    public ProfileDTO toDto() {
        return new ProfileDTO(
                Sanitizer.sanitize(niveau),
                Sanitizer.sanitize(bac),
                Sanitizer.sanitize(duree),
                Sanitizer.sanitize(apprentissage),
                geo_pref.stream().map(Sanitizer::sanitize).collect(Collectors.toSet()),
                spe_classes.stream().map(Sanitizer::sanitize).collect(Collectors.toSet()),
                scores.entrySet().stream().filter(e -> e.getValue() != null && e.getValue() > 0).map(Map.Entry::getKey).toList(),
                Sanitizer.sanitize(moygen),
                (choices == null ? List.of() : choices.values().stream().toList()),
                statut
        );
    }


}
