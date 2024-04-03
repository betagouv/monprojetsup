package fr.gouv.monprojetsup.data.model.thematiques;

import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.update.onisep.ThematiquesOnisep;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public record Thematiques(
        Map<String,String> thematiques,

        Map<String,String> categories,
        Map<String,String> parent

) {

    public static final Set<String> forceSingleton = Set.of(
            "T_ITM_1020", // "sciences humaines et sociales"
            "T_ITM_917" //"lettres - langues"
    );

    public static final Map<String, List<String>> replaceBy = Map.of(
            "T_ITM_832", List.of("T_ITM_1067","T_ITM_PERSO7") // "automatismes" -->  "électronique" et "industrie"
    );

    public static final Set<String> delete = Set.of(
            "T_ITM_1173",//"formations généralistes ou polyvalentes"
            "T_ITM_1340", // "hôtellerie - restauration"
            "T_ITM_793",//fonction production
            "T_ITM_1063"//recherche
    );


    private Thematiques(@NotNull ThematiquesOnisep thematiques) {
        this(new HashMap<>(), new HashMap<>(), new HashMap<>());

        categories.clear();
        categories.putAll(thematiques.categories());

        thematiques.thematiques().values().forEach(m -> {
            String cid = Constants.cleanup(m.id());
            if(!delete.contains( cid)) {
                this.thematiques.put(cid, m.nom());
                String cpar = m.parent() != null ? Constants.cleanup(m.parent()) : null;
                if (cpar != null
                        && !forceSingleton.contains(cpar)
                        && !delete.contains(cpar)
                ) {
                    this.parent.put(cid, cpar);
                }
            }
        });

        thematiques.redirections().forEach((s, s2) -> this.parent.put(Constants.cleanup(s), Constants.cleanup(s2)));
    }

    public static Thematiques load() throws IOException {
        ThematiquesOnisep thematiquesOnisep = ThematiquesOnisep.load();
        return new Thematiques(thematiquesOnisep);
    }

    public @NotNull List<String> representatives(String item) {
        if (item == null) return Collections.emptyList();
        item = Constants.cleanup(item);
        if (replaceBy.containsKey(item)) {
            return replaceBy.get(item).stream().flatMap(it -> representatives(it).stream()).toList();
        }
        if (parent.containsKey(item)) return representatives(parent.get(item));
        return categories.containsKey(item) ? List.of(item) : Collections.emptyList();
    }

    public void retainAll(Set<String> themesUsed) {
        thematiques.keySet().retainAll(themesUsed);
        parent.clear();
    }
}
