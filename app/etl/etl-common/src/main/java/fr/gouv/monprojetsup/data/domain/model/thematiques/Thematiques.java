package fr.gouv.monprojetsup.data.domain.model.thematiques;

import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.model.onisep.DomainesMps;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record Thematiques(

        Map<String,String> categories,
        Map<String,List<String>> parents,

        List<CategorieThematiques> groupes

) {


    public Thematiques(@NotNull DomainesMps thematiques) {
        this(new HashMap<>(), new HashMap<>(), new ArrayList<>());

        Map<String, CategorieThematiques> groupes = new HashMap<>();
        thematiques.regroupements().forEach((s, regroupement) -> {
            val key = regroupement.groupe();
            val emoji = regroupement.emojiGroupe();
            CategorieThematiques cat = groupes.computeIfAbsent(key, k -> new CategorieThematiques(key, emoji, new ArrayList<>()));
            cat.items().add(new CategorieThematiques.Item(s, regroupement.label(), regroupement.emoji()));
        });
        this.groupes.addAll(groupes.values());

    }

    public void retainAll(Set<String> themesUsed) {
        parents.clear();
        groupes.forEach(g -> g.retainAll(themesUsed));
        groupes.removeIf(g -> g.items().isEmpty());
    }

    public int size() {
        return groupes.stream().mapToInt(g -> g.items().size()).sum();
    }

    public Map<String, String> getLabels() {
        val result = new HashMap<String, String>();
        this.groupes.forEach(
                g -> g.items().forEach(
                        item -> result.put(Constants.cleanup(item.key()), item.label())
                )
        );
        return result;
    }
}
