package fr.gouv.monprojetsup.suggestions.algo;

import fr.gouv.monprojetsup.suggestions.dto.SuggestionDTO;
import fr.gouv.monprojetsup.suggestions.dto.explanations.Explanation;
import org.jetbrains.annotations.Nullable;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static fr.gouv.monprojetsup.data.Constants.BR;
import static fr.gouv.monprojetsup.suggestions.dto.SuggestionDTO.SUGG_PENDING;
import static java.lang.System.lineSeparator;

/**
 * A single suggestion. Including, optionally, the explanations.
 */
public record Suggestion(
        String fl,//key, named fl for historical reasons
        @Nullable @Transient List<Explanation> expl,

        String date,
        @Nullable Integer status,


        List<String> items//elements, like filiere in a grouped filiere
) {

    public Suggestion(String fl) {
        this(fl, null, LocalDateTime.now().toString(), SUGG_PENDING, Collections.emptyList());
    }

    public static Suggestion getPendingSuggestion(String fl, List<Explanation> explanations, List<String> items) {
        return new Suggestion(fl, explanations, LocalDateTime.now().toString(), SUGG_PENDING, items);
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

    public String humanReadable(Map<String,String> labels) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t" + labels.getOrDefault(fl,fl));
        sb.append(lineSeparator());
        sb.append(lineSeparator());
        if(expl != null) {
            for (Explanation e : expl) {
                if(e.getTag() != null || e.getDebug() == null) {
                    continue;
                }
                sb.append("\n");
                sb.append(e.toHumanReadable(labels).replace(BR, ""));
            }
        }
        return sb.toString();
    }

    public SuggestionDTO toDTO() {
        return new SuggestionDTO(fl, status, null);
    }
}
