package fr.gouv.monprojetsup.suggestions.algos;

import fr.gouv.monprojetsup.common.Sanitizer;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.common.dto.SuggestionDTO;
import org.jetbrains.annotations.Nullable;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static fr.gouv.monprojetsup.data.Constants.BR;
import static fr.gouv.monprojetsup.common.dto.SuggestionDTO.SUGG_PENDING;
import static java.lang.System.lineSeparator;

/**
 * A single suggestion. Including, optionally, the explanations.
 */
public record Suggestion(
        String fl,//key, named fl for historical reasons
        @Nullable @Transient List<Explanation> expl,

        String date,
        @Nullable Integer status,

        List<String> items//items, like filiere in a grouped filiere
) {

    public Suggestion(String fl) {
        this(fl, null, LocalDateTime.now().toString(), SUGG_PENDING, Collections.emptyList());
    }

    public static Suggestion getPendingSuggestion(String fl, List<Explanation> explanations, List<String> items) {
        return new Suggestion(fl, explanations, LocalDateTime.now().toString(), SUGG_PENDING, items);
    }

    public static Suggestion getSuggestion(Suggestion s, int status) {
        return new Suggestion(s.fl, s.expl, LocalDateTime.now().toString(), status, null);
    }

    public static Suggestion merge(String keyGroup, List<String> keys, @Nullable List<Explanation> explanations) {
        return new Suggestion(
                    keyGroup,
                    Explanation.merge(explanations),
                    LocalDateTime.now().toString(),
                    SUGG_PENDING,
                    keys
                );
    }

    public Suggestion sanitize() {
        return new Suggestion(
                Sanitizer.sanitize(fl),
                null,
                Sanitizer.sanitize(date),
                status,
                items == null ? Collections.emptyList() : items.stream().map(Sanitizer::sanitize).toList()
        );
    }

    public Suggestion anonymize() {
        return new Suggestion(
                fl,
                null,
                null,
                status,
                items
        );
    }

    public String humanReadable() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t" + ServerData.getDebugLabel(fl));
        sb.append(lineSeparator());
        sb.append(lineSeparator());
        if(expl != null) {
            for (Explanation e : expl) {
                if(e.tag != null || e.debug == null) {
                    continue;
                }
                sb.append("\n");
                sb.append(e.toHumanReadable().replace(BR, ""));
            }
        }
        return sb.toString();
    }

    public SuggestionDTO toDTO() {
        return new SuggestionDTO(fl, status, date);
    }
}
