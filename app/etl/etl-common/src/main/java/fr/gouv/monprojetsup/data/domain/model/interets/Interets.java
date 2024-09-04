package fr.gouv.monprojetsup.data.domain.model.interets;

import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.model.rome.InteretsRome;
import lombok.val;

import java.util.*;

import static fr.gouv.monprojetsup.data.domain.model.interets.CategorieInterets.fromMap;

public record Interets(
        List<CategorieInterets> groupeInterets
) {


    public static Interets getInterets(List<Map<String, String>> groupes) {
        val result = new Interets(new ArrayList<>());
        result.groupeInterets.addAll(fromMap(groupes));
        return result;
    }

    public static String getKey(InteretsRome.Item m) {
        return Constants.CENTRE_INTERETS_ROME + Math.abs(m.libelle_centre_interet().hashCode());
    }

    public void retainAll(Set<String> interetsUsed) {
        groupeInterets.forEach(g -> g.retainAll(interetsUsed));
        groupeInterets.removeIf(g -> g.items().isEmpty());
    }

    public int size() {
        return groupeInterets.stream().mapToInt(g -> g.items().size()).sum();
    }

    public Map<String, String> getLabels() {
        val result = new HashMap<String, String>();
        this.groupeInterets.forEach(
                g -> g.items().forEach(
                        item -> {
                            result.put(item.getId(), item.label());
                            item.keys().forEach(
                                    itemKey ->
                                            result.put(item.getId(), item.label())
                            );
                        }
                )
        );
        return result;
    }

    public Map<String, String> getItemVersGroupe() {
        val itemVersGroupe = new HashMap<String, String>();
        this.groupeInterets.forEach(
                g -> g.items().forEach(
                        item -> item.keys().forEach(
                                itemKey ->
                                        itemVersGroupe.put(itemKey, g.getId())
                        )
                )
        );
        return itemVersGroupe;
    }
}
