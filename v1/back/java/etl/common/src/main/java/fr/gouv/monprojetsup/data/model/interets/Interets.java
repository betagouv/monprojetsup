package fr.gouv.monprojetsup.data.model.interets;

import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.onisep.InteretsOnisep;
import fr.gouv.monprojetsup.data.rome.InteretsRome;

import java.util.*;

import static fr.gouv.monprojetsup.data.model.interets.Interets.Groupe.fromMap;

public record Interets(
        /**
         * indexed by interests fl e.g. "T-IDEO2.4819
         */

        Map<String,String> interets,

        List<Groupe> groupes,

        Map<String, List<String>> expansion
) {

    /* we avoid changing labels of groupes alreaduy existing */
    public void put(String key, String label) {
        key = Constants.cleanup(key);
        String finalKey = key;
        label = groupes.stream().flatMap(g -> g.items.stream()).filter(g -> g.keys.contains(finalKey)).findFirst().map(g -> g.label).orElse(label);
        interets.put(key, label);
    }

    public record Item(
            Set<String> keys,
            String label,
            String emoji

    ) {
    }

    public record Groupe(
            String label,
            String emoji,
            List<Item> items
    ) {
        public static List<Groupe> fromMap(List<Map<String, String>> groupes) {
            List<Groupe> res = new ArrayList<>();
            //Source,id,label,surcategorie,regroupement,emoji
            Groupe curGroupe = null;
            Item curItem = null;
            for (Map<String, String> g : groupes) {
                String id = g.getOrDefault("id","");
                String surcategorie = g.getOrDefault("surcategorie","");
                String regroupement = g.getOrDefault("regroupement","");
                String emoji = g.getOrDefault("emoji","");

                //a white line terminates current group and item
                if(id.isEmpty()) {
                    if(curGroupe != null && curGroupe.items.isEmpty()) throw new RuntimeException("Empty groupe " + curGroupe.label);
                    if(curGroupe != null && curItem == null) throw new RuntimeException("Empty item in groupe " + curGroupe.label);
                    if(curGroupe != null && curItem != null && curItem.keys.isEmpty()) throw new RuntimeException("Empty keys in item " + curItem.label);
                    curGroupe = null;
                    curItem = null;
                }
                //new group
                if(!regroupement.isEmpty()) {
                    curGroupe = new Groupe(regroupement, emoji, new ArrayList<>());
                    res.add(curGroupe);
                }
                //new item
                if(!surcategorie.isEmpty()) {
                    curItem = new Item(new HashSet<>(), surcategorie, emoji);
                    if(curGroupe==null) throw new RuntimeException("No groupe for surcategorie " + surcategorie);
                    curGroupe.items.add(curItem);
                }
                //add element t current item
                if(!id.isEmpty()) {
                    if(curGroupe==null) {
                        throw new RuntimeException("No groupe for id " + id);
                    }
                    if(curItem==null) {
                        curItem = new Item(new HashSet<>(), curGroupe.label, emoji);
                        curGroupe.items.add(curItem);
                    }
                    curItem.keys.add(id);
                }
            }
            return res;
        }
    }

    public Interets(InteretsOnisep interets, List<Map<String, String>> groupes) {
        this(new HashMap<>(), new ArrayList<>(), new HashMap<>());
        interets.interets().forEach(m -> {
            this.interets.put(Constants.cleanup(m.id()), m.libelle());
        });
        this.groupes.addAll(fromMap(groupes));
        this.groupes.forEach(
                g -> g.items.stream().forEach(
                        item -> item.keys().forEach(
                                i ->
                                {
                                    expansion.computeIfAbsent(i, k -> new ArrayList<>()).addAll(item.keys);
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
