package fr.gouv.monprojetsup.data.domain.model.interets;

import fr.gouv.monprojetsup.data.domain.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

public record CategorieInterets(
        @NotNull String label,
        @NotNull String emoji,
        List<@NotNull Item> items
) {
    private static final Logger LOGGER = Logger.getLogger(CategorieInterets.class.getSimpleName());

    @NotNull
    public String getId() {
        return Constants.cleanup(
                label.replace("je veux", "").trim()
        ).trim();
    }

    public String getFrontLabel() {
        return label.replace("je veux", "").trim();
    }

    public static List<CategorieInterets> fromMap(
            @NotNull List<Map<String, @NotNull String>> groupes,
            Map<String,String> labels
    ) {


        List<CategorieInterets> res = new ArrayList<>();
        //Source,id,label,surcategorie,regroupement,emoji
        CategorieInterets curGroupeInterets = null;
        Item curItem = null;
        //ordre issu de la source
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
                if (curGroupeInterets != null && curItem.subKeyslabels.isEmpty())
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
                curItem = new Item(new HashMap<>(), surcategorie, emoji);
                if (curGroupeInterets == null) throw new RuntimeException("No groupe for surcategorie " + surcategorie);
                curGroupeInterets.items.add(curItem);
            }
            //add element t current item
            if (!id.isEmpty()) {
                if (curGroupeInterets == null) {
                    throw new RuntimeException("No groupe for id " + id);
                }
                if (curItem == null) {
                    curItem = new Item(new HashMap<>(), curGroupeInterets.getFrontLabel(), emoji);
                    curGroupeInterets.items.add(curItem);
                }
                curItem.subKeyslabels.put(id,labels.getOrDefault(id,id));
            }
        }
        return res;
    }

    public void retainAll(Set<String> interetsUsed) {
        if (interetsUsed.contains(getId())) return;
        List<@NotNull Item> toRemove =
                items.stream()
                        .filter(it -> it.subKeyslabels.keySet().stream().noneMatch(k -> interetsUsed.contains(Constants.cleanup(k))))
                        .toList();
        toRemove.forEach(item -> LOGGER.info("Intérêt non utilisé: " + item));
        items.removeAll(toRemove);
    }

    public record Item(
            @NotNull Map<String,@NotNull String> subKeyslabels,
            @NotNull String label,
            @NotNull String emoji

    ) {

        public String getId() {
            return Constants.cleanup(getFrontLabel()).trim();
        }

        public String getFrontLabel() {
            return label.replace("je veux", "").trim();
        }

    }
}
