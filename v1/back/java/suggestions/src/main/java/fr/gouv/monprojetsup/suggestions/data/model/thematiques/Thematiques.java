package fr.gouv.monprojetsup.suggestions.data.model.thematiques;

import fr.gouv.monprojetsup.suggestions.data.Constants;
import fr.gouv.monprojetsup.suggestions.data.DataSources;
import fr.gouv.monprojetsup.suggestions.data.tools.csv.CsvTools;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record Thematiques(
        Map<String,String> thematiques,

        Map<String,String> categories,
        Map<String,List<String>> parents,

        List<Category> groupes

) {
    public record Item (
            String key,
            String label,
            String emoji
    ) {
    }

    public record Category(
            String label,
            String emoji,

            List<Item> items
    ) {

        public boolean contains(String item) {
            return items.stream().anyMatch(i -> i.key.equals(item));
        }
    }


    public static List<Category> loadthematiques() {
        Map<String, Category> groupes = new HashMap<>();
        List<Category> categories = new ArrayList<>();

        String groupe = "";
        String emojig = "";
        for (Map<String, String> stringStringMap : CsvTools.readCSV(DataSources.getSourceDataFilePath(DataSources.THEMATIQUES_REGROUPEMENTS_PATH), '\t')) {
            String id = stringStringMap.get("id").trim();
            String regroupement = stringStringMap.get("regroupement").trim();
            if(!regroupement.isEmpty()) {
                groupe = regroupement;
                String emojiGroupe = stringStringMap.get("Emoji");
                if(!emojiGroupe.isEmpty()) {
                    emojig = emojiGroupe;
                } else {
                    throw new RuntimeException("Groupe " + groupe + " sans emoji dans " + DataSources.THEMATIQUES_REGROUPEMENTS_PATH);
                }
            }
            String emoji = stringStringMap.getOrDefault("Emojis","").trim();
            String label = stringStringMap.getOrDefault("label","").trim();
            if(groupe.isEmpty()) throw new RuntimeException("Groupe vide dans " + DataSources.THEMATIQUES_REGROUPEMENTS_PATH);
            if(emojig.isEmpty()) throw new RuntimeException("Groupe sans emoji dans " + DataSources.THEMATIQUES_REGROUPEMENTS_PATH);
            Category cat = groupes.get(groupe);
            if(cat  == null) {
                cat = new Category(groupe, emojig, new ArrayList<>());
                groupes.put(groupe, cat);
                categories.add(cat);
            }
            cat.items.add(new Item(id, label, emoji));
        }
        return categories;
    }

    public @NotNull List<String> representatives(String dirtyItem) {
        if (dirtyItem == null) return Collections.emptyList();
        final String item = Constants.cleanup(dirtyItem);
        if (parents.containsKey(item)) return parents.get(item).stream().flatMap(parent -> representatives(parent).stream()).toList();
        if(groupes.stream().anyMatch(g -> g.contains(item))) return List.of(item);
        return Collections.emptyList();
    }

}
