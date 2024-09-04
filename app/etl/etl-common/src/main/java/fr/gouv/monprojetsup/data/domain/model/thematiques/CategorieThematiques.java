package fr.gouv.monprojetsup.data.domain.model.thematiques;

import fr.gouv.monprojetsup.data.domain.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public record CategorieThematiques(
        String label,
        String emoji,

        List<Item> items
) {

    public boolean contains(String item) {
        return items.stream().anyMatch(i -> i.key.equals(item));
    }

    public @NotNull String getMpsId() {
        return Constants.cleanup(label);
    }

    public void retainAll(Set<String> themesUsed) {
        if(themesUsed.contains(getMpsId())) return;
        items.removeIf(it -> !themesUsed.contains(it.key));
    }

    public record Item (
            String key,
            String label,
            String emoji
    ) {
        public String getMpsId() {
            return Constants.cleanup(key);
        }

    }
}
