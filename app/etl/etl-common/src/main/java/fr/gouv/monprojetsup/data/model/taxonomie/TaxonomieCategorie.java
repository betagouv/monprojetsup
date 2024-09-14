package fr.gouv.monprojetsup.data.model.taxonomie;

import fr.gouv.monprojetsup.data.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

public record TaxonomieCategorie(
        @NotNull String label,
        @NotNull String emoji,
        List<@NotNull TaxonomieElement> elements
) {
    private static final Logger LOGGER = Logger.getLogger(TaxonomieCategorie.class.getSimpleName());

    @NotNull
    public String getId() {
        return Constants.cleanup(label);
    }


    public void retainAll(Set<String> interetsUsed) {
        if (interetsUsed.contains(getId())) return;
        List<@NotNull TaxonomieElement> toRemove =
                elements.stream()
                        .filter(it -> it.atomes.keySet().stream().noneMatch(k -> interetsUsed.contains(Constants.cleanup(k))))
                        .toList();
        toRemove.forEach(item -> LOGGER.info("Intérêt non utilisé: " + item));
        elements.removeAll(toRemove);
    }

    public record TaxonomieElement(
            @NotNull Map<String,@NotNull String> atomes,
            @NotNull String label,
            @NotNull String emoji,
            @NotNull String description


    ) {

        public String getId() {
            return Constants.cleanup(label).trim();
        }

    }
}
