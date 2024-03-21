package fr.gouv.monprojetsup.data.model.tags;

import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.web.server.WebServerConfig;
import fr.gouv.monprojetsup.web.server.WebServer;

import java.io.IOException;
import java.util.*;

import static fr.gouv.monprojetsup.data.Constants.gFlCodToFrontId;
import static fr.gouv.monprojetsup.tools.Serialisation.fromJsonFile;

public record TagsSources(
        Map<String, Set<String>> sources
) {
    public TagsSources() {
        this(new TreeMap<>()); //using a tree map we get an output sorted alphabetically
    }

    public static TagsSources load(Map<String, String> groups) throws IOException {
        WebServer.LOGGER.info("Chargement des sources des mots-clés, et extension via la correspondance");
        TagsSources tags = fromJsonFile(DataSources.getSourceDataFilePath(DataSources.TAGS_SOURCE_MERGED_FILENAME), TagsSources.class);
        tags.sources.computeIfAbsent("pass", z -> new HashSet<>()).add(gFlCodToFrontId(WebServerConfig.PASS_FL_COD));
        tags.extendToGroups(groups);
        return tags;
    }

    public void add(String tag, Set<String> tagSources) {
        sources.put(tag, tagSources);
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
}
