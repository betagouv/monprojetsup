package fr.gouv.monprojetsup.suggestions.infrastructure.model.tags;

import java.util.Set;

public record TagGroup(Set<String> tags, Set<String> selected) {

}
