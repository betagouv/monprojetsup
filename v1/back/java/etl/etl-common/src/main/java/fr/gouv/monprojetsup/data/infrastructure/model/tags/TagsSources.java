package fr.gouv.monprojetsup.data.infrastructure.model.tags;

import fr.gouv.monprojetsup.data.infrastructure.DataSources;
import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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
        if (tag != null && tag.length() >= 3) {
            sources.computeIfAbsent(tag, z -> new HashSet<>()).add(source);
        }
    }

    public void clear() {
        sources.clear();
    }

    // Added by Benoît : 
    public Set<String> getTags() {
        return sources.keySet();
    }

    public void remove(String tag) {
        sources.remove(tag);
    }

    public void putAll(TagsSources tags) {
        this.sources.putAll(tags.sources);
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

    @NotNull
    public static TagsSources loadTagsSources(@NotNull Map<String,String> groups, @NotNull DataSources sources) throws IOException {
            //log.info("Chargement des sources des mots-clés, et extension via la correspondance");
            val tags = Serialisation.fromJsonFile(
                    sources.getSourceDataFilePath(DataSources.TAGS_SOURCE_MERGED_FILENAME),
                    TagsSources.class);

            tags.sources.computeIfAbsent("pass", z -> new HashSet<>()).add(Constants.gFlCodToFrontId(Constants.PASS_FL_COD));

            tags.extendToGroups(groups);
            return tags;
    }
}
