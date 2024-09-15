package fr.gouv.monprojetsup.data.model.taxonomie;

import fr.gouv.monprojetsup.data.Constants;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static fr.gouv.monprojetsup.data.Constants.includeKey;

public record Taxonomie(
        List<TaxonomieCategorie> categories
) {

    public void retainAll(Set<String> interetsUsed) {
        categories.forEach(g -> g.retainAll(interetsUsed));
        categories.removeIf(g -> g.elements().isEmpty());
    }

    public int size() {
        return categories.stream().mapToInt(g -> g.elements().size()).sum();
    }

    public Map<String, String> getLabels(boolean includeKeys) {
        val result = new HashMap<String, String>();
        this.categories.forEach(
                g -> g.elements().forEach(
                        item -> result.put(item.id, includeKeys ? includeKey(item.id, item.label()) : item.label())
                )
        );
        return result;
    }

    public Stream<Pair<String,String>> getAtomesversElements() {
        return categories.stream()
                .flatMap(g -> g.elements().stream()
                        .flatMap(item -> item.atomes().keySet().stream()
                                .map(atomeKey -> Pair.of(atomeKey, item.id))
                        )
                );
    }

    @NotNull
    public Collection<@NotNull String> getElementIds() {
        return categories.stream()
                .flatMap(g -> g.elements().stream())
                .map(TaxonomieCategorie.TaxonomieElement::id)
                .distinct()
                .toList();
    }

    @NotNull
    public Collection<@NotNull String> getAtomesIds() {
        return categories.stream()
                .flatMap(g -> g.elements().stream())
                .flatMap(item -> item.atomes().keySet().stream())
                .distinct()
                .toList();
    }


    public record TaxonomieCategorie(
            @NotNull String label,
            @NotNull String emoji,
            List<@NotNull TaxonomieElement> elements
    ) {
        private static final Logger LOGGER = Logger.getLogger(TaxonomieCategorie.class.getSimpleName());

        public TaxonomieCategorie(String categorieLabel, String emoji) {
            this(capitalizeFirstLetter(categorieLabel), emoji, new ArrayList<>());
        }

        @NotNull
        public String getId() {
            return Constants.cleanup(label.toLowerCase());
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
                @NotNull String id,
                @NotNull Map<String,@NotNull String> atomes,
                @NotNull String label,
                @NotNull String emoji,
                @Nullable String description


        ) {

            public static TaxonomieElement build(
                    @NotNull String id,
                    @NotNull Map<String,@NotNull String> atomes,
                    @NotNull String label,
                    @NotNull String emoji,
                    @Nullable  String description) {
                return new TaxonomieElement(id, atomes, capitalizeFirstLetter(label), emoji, description);
            }
        }
    }

    public static @NotNull String capitalizeFirstLetter(@NotNull String sentence) {
        // Trim leading and trailing spaces
        sentence = sentence.trim();
        if (sentence.isEmpty()) {
            return sentence;
        }
        // Capitalize the first character and concatenate it with the rest of the string
        return sentence.substring(0, 1).toUpperCase() + sentence.substring(1);
    }

}
