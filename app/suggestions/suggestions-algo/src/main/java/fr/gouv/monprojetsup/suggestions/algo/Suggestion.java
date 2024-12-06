package fr.gouv.monprojetsup.suggestions.algo;

import fr.gouv.monprojetsup.suggestions.dto.ChoiceDTO;
import fr.gouv.monprojetsup.suggestions.dto.explanations.Explanation;
import org.jetbrains.annotations.Nullable;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static fr.gouv.monprojetsup.suggestions.Constants.BR;
import static fr.gouv.monprojetsup.suggestions.dto.ChoiceDTO.SUGG_PENDING;
import static java.lang.System.lineSeparator;

/**
 * A single suggestion. Including, optionally, the explanations.
 */
public record Suggestion(
        String id,
        @Nullable @Transient List<Explanation> expl,

        String date,
        @Nullable Integer status
) {

    public Suggestion(String fl) {
        this(fl, null, LocalDateTime.now().toString(), SUGG_PENDING);
    }

    public static Suggestion getPendingSuggestion(String fl, List<Explanation> explanations) {
        return new Suggestion(fl, explanations, LocalDateTime.now().toString(), SUGG_PENDING);
    }

    public String humanReadable(Map<String,String> labels) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t" + labels.getOrDefault(id, id));
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

    public ChoiceDTO toDTO() {
        return new ChoiceDTO(id, status, null);
    }
}
