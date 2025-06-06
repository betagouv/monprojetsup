package fr.gouv.monprojetsup.data.model.tags;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

@Slf4j
public record TagsFormations(
        Map<String, Set<String>> tags
) {
}
