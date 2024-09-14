package fr.gouv.monprojetsup.data.domain.model.interets;

import lombok.val;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.gouv.monprojetsup.data.domain.Constants.includeKey;

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

    public Map<String, String> getItemVersGroupe() {
        val itemVersGroupe = new HashMap<String, String>();
        this.categories.forEach(
                g -> g.elements().forEach(
                        item -> item.atomes().keySet().forEach(
                                itemKey ->
                                        itemVersGroupe.put(itemKey, g.getId())
                        )
                )
        );
        return itemVersGroupe;
    }
}
