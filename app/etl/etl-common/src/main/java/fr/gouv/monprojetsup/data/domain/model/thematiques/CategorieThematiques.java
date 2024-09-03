package fr.gouv.monprojetsup.data.domain.model.thematiques;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public record CategorieThematiques(
        String label,
        String emoji,

        List<Item> items
) {

    public boolean contains(String item) {
        return items.stream().anyMatch(i -> i.key.equals(item));
    }

    public @NotNull String getId() {
        return items.stream().map(Item::key).sorted().collect(Collectors.joining("_"));
    }

    public record Item (
            String key,
            String label,
            String emoji
    ) {
    }
}
