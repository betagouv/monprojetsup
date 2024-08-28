package fr.gouv.monprojetsup.data.domain.model.tags;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public record TagsSources(
        Map<String, Set<String>> sources
) {

    public TagsSources() {
        this(new TreeMap<>()); //using a tree map we get an output sorted alphabetically
    }

    public void add(String tag, Set<String> tagSources) {
        sources.put(tag, tagSources);
    }

    public void add(String tag, String source) {
        if (tag != null && source != null) {
            sources.computeIfAbsent(tag, z -> new HashSet<>()).add(source);
        }
    }

    public Set<String> getTags() {
        return sources.keySet();
    }

    public void remove(String tag) {
        sources.remove(tag);
    }

    public void extendToGroups(Map<String, String> correspondances) {
        //les groupes héritent des mots-clés des enfants
        sources.forEach((tag, keys) -> {
            List<String> newSources = keys.stream()
                    .map(correspondances::get)
                    .filter(Objects::nonNull)
                    .toList();
            keys.addAll(newSources);
        });

    }

    public static String normalize(String tag) {
        return Normalizer.normalize(
                        tag
                                .toLowerCase()
                                .replaceAll("[-/]", " ")
                                .trim(),
                        Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}\\s]", "");

    }

    public void normalize() {
        val copy = new HashMap<>(sources);
        sources.clear();
        copy.forEach((tag, sources) -> {
            val normalizedTag = normalize(tag);
            this.sources.computeIfAbsent(normalizedTag, z -> new HashSet<>()).addAll(sources);
        });
    }

    @NotNull
    public Map<String, List<String>> getKeyToTags() {
        Map<String, Set<String>> result = new HashMap<>();
        sources.forEach((tag, keys) -> keys.forEach(key -> result.computeIfAbsent(key, z -> new HashSet<>()).add(tag.toLowerCase().trim())));
        return result.entrySet().stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                e -> new ArrayList<>(e.getValue())
                        )
                );
    }

    public void set(TagsSources motsCles) {
        sources.clear();
        sources.putAll(motsCles.sources);
    }
}
