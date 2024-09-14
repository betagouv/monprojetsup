package fr.gouv.monprojetsup.data.model.taxonomie;

import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static fr.gouv.monprojetsup.data.Constants.includeKey;

public record Taxonomie(
        List<TaxonomieCategorie> categories
) {

    public void retainAll(Set<String> interetsUsed) {
        categories.forEach(g -> g.retainAll(interetsUsed));
        categories.removeIf(g -> g.elements().isEmpty());
    }

    public int size() {
        return categories.stream().mapToInt(g -> g.elements().size()).sum();
    }

    public Map<String, String> getLabels(boolean includeKeys) {
        val result = new HashMap<String, String>();
        this.categories.forEach(
                g -> g.elements().forEach(
                        item -> {
                            result.put(item.getId(), includeKeys ? includeKey(item.getId(), item.label()) : item.label());
                        }
                )
        );
        return result;
    }

    public Stream<Pair<String,String>> getItemVersGroupe() {
        return categories.stream()
                .flatMap(g -> g.elements().stream()
                        .flatMap(item -> item.atomes().keySet().stream()
                                .map(itemKey -> Pair.of(itemKey, g.getId()))
                        )
                );
    }
}
