package fr.gouv.monprojetsup.data.infrastructure.model.tags;

import java.util.Set;

public record TagGroup(Set<String> tags, Set<String> selected) {

}
