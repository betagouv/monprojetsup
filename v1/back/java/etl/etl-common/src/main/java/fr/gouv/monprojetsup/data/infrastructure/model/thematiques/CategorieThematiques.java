package fr.gouv.monprojetsup.data.infrastructure.model.thematiques;

import java.util.List;

public record CategorieThematiques(
        String label,
        String emoji,

        List<Item> items
) {

    public boolean contains(String item) {
        return items.stream().anyMatch(i -> i.key.equals(item));
    }

    public record Item (
            String key,
            String label,
            String emoji
    ) {
    }
}
