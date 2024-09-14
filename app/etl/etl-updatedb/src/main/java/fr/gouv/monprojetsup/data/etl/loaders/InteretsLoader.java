package fr.gouv.monprojetsup.data.etl.loaders;

import fr.gouv.monprojetsup.data.model.taxonomie.TaxonomieCategorie;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteretsLoader {

    public static List<TaxonomieCategorie> getTaxonomieCategories(
            @NotNull List<Map<String, @NotNull String>> lignesCsv
    ) {
        List<TaxonomieCategorie> res = new ArrayList<>();
        //Source,id,label,surcategorie,regroupement,emoji
        TaxonomieCategorie categorie = null;
        TaxonomieCategorie.TaxonomieElement element = null;
        //ordre issu de la source
        for (Map<String, String> g : lignesCsv) {
            //une ligne = 1 atome, sauf les lignes blanches qui terminent un groupe
            String atomeKey = g.getOrDefault("id", "");
            String atomeLabel = g.getOrDefault("label", "");
            String categorieLabel = g.getOrDefault("regroupement", "").replace("je veux", "");
            String elementLabel = g.getOrDefault("surcategorie", "").replace("je veux", "");
            String emoji = g.getOrDefault("emoji", "");

            //a white line terminates current group and item
            if (!categorieLabel.isEmpty()) {
                if (emoji.isBlank()) {
                    throw new RuntimeException(" Regroupement " + categorieLabel + "sans emoji");
                }
                if (categorie != null && categorie.elements().isEmpty())
                    throw new RuntimeException("Regroupement vide " + categorie.label());
                if (categorie != null && element == null)
                    throw new RuntimeException("Element vide " + categorie.label());
                if (categorie != null && element.atomes().isEmpty())
                    throw new RuntimeException("Element sans atome " + element.label());
                categorie = new TaxonomieCategorie(categorieLabel, emoji, new ArrayList<>());
                res.add(categorie);
            }
            if(categorieLabel.isBlank() && atomeKey.isBlank() ) {
                categorie = null;
                element = null;
                continue;
            }
            if(element == null && elementLabel.isBlank()  && !atomeKey.isBlank()) {
                //monocategorie
                elementLabel = categorieLabel;
            }
            if (!elementLabel.isEmpty()) {
                if (categorie == null) {
                    throw new RuntimeException("Pas de catégorie pour l'élément " + elementLabel);
                }
                if (emoji.isBlank()) {
                    throw new RuntimeException(" Element " + elementLabel + "sans emoji");
                }
                if (element != null && element.atomes().isEmpty()) {
                    throw new RuntimeException("Element sans atome " + element.label());
                }
                element = new TaxonomieCategorie.TaxonomieElement(new HashMap<>(), elementLabel, emoji);
                categorie.elements().add(element);
            }
            if (!atomeKey.isBlank()) {
                if (atomeLabel.isBlank()) {
                    throw new RuntimeException("Atome sans label " + atomeKey);
                }
                if (element == null) {
                    throw new RuntimeException("Pas d'élément pour l'atome " + atomeKey);
                }
                element.atomes().put(atomeKey, atomeLabel);
            }
        }
        return res;
    }


    private InteretsLoader() {
    }

}
