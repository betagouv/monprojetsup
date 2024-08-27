package fr.gouv.monprojetsup.data.domain.model.specialites;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public record Specialites(
        String source,
        Map<@NotNull Integer,@NotNull String> specialites,
        Map<@NotNull String, @NotNull Set<@NotNull Integer>> specialitesParBac,
        Set<@NotNull String> specialitesEpreuvesBac )
{
    public Specialites() {
        this("", new HashMap<>(), new HashMap<>(), new HashSet<>());
    }

    public boolean isSpecialite(int key) {
        return specialites.containsKey(key);
    }

    @NotNull
    public List<@NotNull String> getBacs(int key) {
        return specialitesParBac.entrySet().stream()
                .filter(e -> e.getValue().contains(key))
                .map(Map.Entry::getKey).toList();
    }
}
