package fr.gouv.monprojetsup.data.domain.model.interets;

import lombok.val;

import java.util.*;

import static fr.gouv.monprojetsup.data.domain.Constants.includeKey;
import static fr.gouv.monprojetsup.data.domain.model.interets.CategorieInterets.fromMap;

public record Interets(
        List<CategorieInterets> groupeInterets
) {


    public static Interets getInterets(List<Map<String, String>> groupes, Map<String,String> labels) {
        val result = new Interets(new ArrayList<>());
        result.groupeInterets.addAll(fromMap(groupes,labels));
        return result;
    }



    public void retainAll(Set<String> interetsUsed) {
        groupeInterets.forEach(g -> g.retainAll(interetsUsed));
        groupeInterets.removeIf(g -> g.items().isEmpty());
    }

    public int size() {
        return groupeInterets.stream().mapToInt(g -> g.items().size()).sum();
    }

    public Map<String, String> getLabels(boolean includeKeys) {
        val result = new HashMap<String, String>();
        this.groupeInterets.forEach(
                g -> g.items().forEach(
                        item -> {
                            result.put(item.getId(), includeKeys ? includeKey(item.getId(), item.label()) : item.label());
                        }
                )
        );
        return result;
    }

    public Map<String, String> getItemVersGroupe() {
        val itemVersGroupe = new HashMap<String, String>();
        this.groupeInterets.forEach(
                g -> g.items().forEach(
                        item -> item.subKeyslabels().keySet().forEach(
                                itemKey ->
                                        itemVersGroupe.put(itemKey, g.getId())
                        )
                )
        );
        return itemVersGroupe;
    }
}
