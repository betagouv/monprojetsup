package fr.gouv.monprojetsup.data.domain.model.thematiques;

import fr.gouv.monprojetsup.data.domain.model.onisep.ThematiquesOnisep;
import fr.gouv.monprojetsup.data.domain.Constants;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record Thematiques(
        Map<String,String> thematiques,

        Map<String,String> categories,
        Map<String,List<String>> parents,

        List<CategorieThematiques> groupes

) {


    public Thematiques(@NotNull ThematiquesOnisep thematiques) {
        this(new HashMap<>(), new HashMap<>(), new HashMap<>(), new ArrayList<>());

        thematiques.thematiques().values().forEach(m -> {
            String cid = Constants.cleanup(m.id());
                this.thematiques.put(cid, m.nom());
                String cpar = m.parent() != null ? Constants.cleanup(m.parent()) : null;
                if (cpar != null)
                    addParent(cid, cpar);
        });

        thematiques.redirections().forEach(
                p -> addParent(p.getLeft(),p.getRight())
        );

        Map<String, CategorieThematiques> groupes = new HashMap<>();
        thematiques.regroupements().forEach((s, regroupement) -> {
            val key = regroupement.groupe();
            val emoji = regroupement.emojiGroupe();
            CategorieThematiques cat = groupes.computeIfAbsent(key, k -> new CategorieThematiques(key, emoji, new ArrayList<>()));
            cat.items().add(new CategorieThematiques.Item(s, regroupement.label(), regroupement.emoji()));
            //maj des labels au passage
            this.thematiques.put(s, regroupement.label());
        });
        this.groupes.addAll(groupes.values());

    }

    private void addParent(String child, String parent) {
        parents.computeIfAbsent(child, z -> new ArrayList<>()).add(parent);
    }

    public @NotNull List<String> representatives(String dirtyItem) {
        if (dirtyItem == null) return Collections.emptyList();
        final String item = Constants.cleanup(dirtyItem);
        if (parents.containsKey(item)) return parents.get(item).stream().flatMap(parent -> representatives(parent).stream()).toList();
        if(groupes.stream().anyMatch(g -> g.contains(item))) return List.of(item);
        return Collections.emptyList();
    }

    public void retainAll(Set<String> themesUsed) {
        thematiques.keySet().retainAll(themesUsed);
        parents.clear();
    }
}
