package fr.gouv.monprojetsup.data.domain.model.interets;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public record CategorieInterets(
        @NotNull String label,
        @NotNull String emoji,
        List<@NotNull Item> items
) {
    public static List<CategorieInterets> fromMap(@NotNull List<Map<String, @NotNull String>> groupes) {
        List<CategorieInterets> res = new ArrayList<>();
        //Source,id,label,surcategorie,regroupement,emoji
        CategorieInterets curGroupeInterets = null;
        Item curItem = null;
        for (Map<String, String> g : groupes) {
            String id = g.getOrDefault("id", "");
            String surcategorie = g.getOrDefault("surcategorie", "");
            String regroupement = g.getOrDefault("regroupement", "");
            String emoji = g.getOrDefault("emoji", "");

            //a white line terminates current group and item
            if (id.isEmpty()) {
                if (curGroupeInterets != null && curGroupeInterets.items.isEmpty())
                    throw new RuntimeException("Empty groupe " + curGroupeInterets.label);
                if (curGroupeInterets != null && curItem == null)
                    throw new RuntimeException("Empty item in groupe " + curGroupeInterets.label);
                if (curGroupeInterets != null && curItem != null && curItem.keys.isEmpty())
                    throw new RuntimeException("Empty keys in item " + curItem.label);
                curGroupeInterets = null;
                curItem = null;
            }
            //new group
            if (!regroupement.isEmpty()) {
                curGroupeInterets = new CategorieInterets(regroupement, emoji, new ArrayList<>());
                res.add(curGroupeInterets);
            }
            //new item
            if (!surcategorie.isEmpty()) {
                curItem = new Item(new HashSet<>(), surcategorie, emoji);
                if (curGroupeInterets == null) throw new RuntimeException("No groupe for surcategorie " + surcategorie);
                curGroupeInterets.items.add(curItem);
            }
            //add element t current item
            if (!id.isEmpty()) {
                if (curGroupeInterets == null) {
                    throw new RuntimeException("No groupe for id " + id);
                }
                if (curItem == null) {
                    curItem = new Item(new HashSet<>(), curGroupeInterets.label, emoji);
                    curGroupeInterets.items.add(curItem);
                }
                curItem.keys.add(id);
            }
        }
        return res;
    }

    public record Item(
            @NotNull Set<@NotNull String> keys,
            @NotNull String label,
            @NotNull String emoji

    ) {
    }
}
