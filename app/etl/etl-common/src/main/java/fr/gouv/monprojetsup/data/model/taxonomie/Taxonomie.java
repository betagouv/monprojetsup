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
                        item -> result.put(item.getId(), includeKeys ? includeKey(item.getId(), item.label()) : item.label())
                )
        );
        return result;
    }

    public Stream<Pair<String,String>> getItemVersGroupe() {
        return categories.stream()
                .flatMap(g -> g.elements().stream()
                        .flatMap(item -> item.atomes().keySet().stream()
                                .map(itemKey -> Pair.of(itemKey, g.getId()))
                        )
                );
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
                @Nullable String description


        ) {

            public String getId() {
                return Constants.cleanup(label).trim();
            }

            public static TaxonomieElement build(
                    @NotNull Map<String,@NotNull String> atomes,
                    @NotNull String label,
                    @NotNull String emoji,
                    @Nullable  String description) {
                return new TaxonomieElement(atomes, capitalizeFirstLetter(label), emoji, description);
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
