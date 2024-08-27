package fr.gouv.monprojetsup.data.infrastructure.model.interets;

import fr.gouv.monprojetsup.data.infrastructure.onisep.InteretsOnisep;
import fr.gouv.monprojetsup.data.infrastructure.rome.InteretsRome;
import fr.gouv.monprojetsup.data.domain.Constants;

import java.util.*;

import static fr.gouv.monprojetsup.data.infrastructure.model.interets.CategorieInterets.fromMap;

public record Interets(
        /**
         * indexed by interests fl e.g. "T-IDEO2.4819
         */
        Map<String,String> interets,

        List<CategorieInterets> groupeInterets,

        Map<String, List<String>> expansion
) {

    /* we avoid changing labels of groupes already existing */
    public void put(String key, String label) {
        key = Constants.cleanup(key);
        String finalKey = key;
        label = groupeInterets.stream()
                .flatMap(g -> g.items().stream())
                .filter(
                        g -> g.keys().contains(finalKey)
                )
                .findFirst().map(CategorieInterets.Item::label).orElse(label);
        interets.put(key, label);
    }

    public Interets(InteretsOnisep interets, List<Map<String, String>> groupes) {
        this(new HashMap<>(), new ArrayList<>(), new HashMap<>());
        interets.interets().forEach(m -> {
            this.interets.put(Constants.cleanup(m.id()), m.libelle());
        });
        this.groupeInterets.addAll(fromMap(groupes));
        this.groupeInterets.forEach(
                g -> g.items().forEach(
                        item -> item.keys().forEach(
                                i ->
                                {
                                    expansion.computeIfAbsent(i, k -> new ArrayList<>()).addAll(item.keys());
                                    //maj du libellé
                                    this.interets.put(Constants.cleanup(i), item.label());
                                }
                        )
                )
        );
    }

    public static String getKey(InteretsRome.Item m) {
        return Constants.CENTRE_INTERETS_ROME + Math.abs(m.libelle_centre_interet().hashCode());
    }

}