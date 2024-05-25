package fr.gouv.monprojetsup.data.model.tags;

import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.ServerData;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.text.Normalizer;
import java.util.*;

import static fr.gouv.monprojetsup.data.Constants.PASS_FL_COD;
import static fr.gouv.monprojetsup.data.Constants.gFlCodToFrontId;
import static fr.gouv.monprojetsup.data.tools.Serialisation.fromJsonFile;
import static java.lang.Math.max;

@Slf4j
public record TagsSources(
        Map<String, Set<String>> sources
) {
    public TagsSources() {
        this(new TreeMap<>()); //using a tree map we get an output sorted alphabetically
    }

    public static TagsSources load(Map<String, String> groups) throws IOException {
        log.info("Chargement des sources des mots-clés, et extension via la correspondance");
        TagsSources tags = fromJsonFile(DataSources.getSourceDataFilePath(DataSources.TAGS_SOURCE_MERGED_FILENAME), TagsSources.class);
        tags.sources.computeIfAbsent("pass", z -> new HashSet<>()).add(gFlCodToFrontId(PASS_FL_COD));
        tags.extendToGroups(groups);
        return tags;
    }

    public void add(String tag, Set<String> tagSources) {
        sources.put(tag, tagSources);
    }

    public void add(String tag, String source) {
        if(tag != null && tag.length() >= 3) {
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

    public Map<String,Integer> getScores(List<String> tags, Set<String> candidates) {

        Map<String,Integer> result = new HashMap<>();

        for(String tag : tags) {

            String cleanTag = normalize(tag);

            candidates.forEach(candidate -> {
                String label = ServerData.getLabel(candidate);
                if(label != null) {
                    String cleanLabel = normalize(label);
                    if(cleanLabel.startsWith(cleanTag) || cleanLabel.contains(" " + cleanTag) || cleanLabel.contains("(" + cleanTag) ) {
                        result.put(candidate, max(result.getOrDefault(candidate, 0) , 8));
                    }
                }
            });

            for (Map.Entry<String, Set<String>> entry : sources.entrySet()) {
                String k = entry.getKey();
                Set<String> value = entry.getValue();
                int score = 0;
                if (k.equals(cleanTag)) score = 4;
                else if (k.startsWith(cleanTag)) score = 2;
                else if(k.contains(cleanTag)) score = 1;
                if(score > 0) {
                    for (String candidate : value) {
                        if (candidates.contains(candidate)) {
                            result.put(candidate, result.getOrDefault(candidate, 0) | score );
                        }
                    }
                }
            }
        }

        ServerData.reverseFlGroups.forEach((flGroup, fls) -> {
            int score = fls.stream().map(fl -> result.getOrDefault(fl, 0)).reduce(0, Integer::max);
            score = max(result.getOrDefault(flGroup, 0), score);
            if(score > 0) {
                result.put(flGroup, score);
            }
        });

        return result;
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

}
