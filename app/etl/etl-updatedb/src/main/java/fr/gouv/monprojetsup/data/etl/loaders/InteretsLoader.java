package fr.gouv.monprojetsup.data.etl.loaders;

import fr.gouv.monprojetsup.data.model.taxonomie.Taxonomie;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteretsLoader {

    public static List<Taxonomie.TaxonomieCategorie> getTaxonomieCategories(
            @NotNull List<Map<String, @NotNull String>> lignesCsv
    ) {
        List<Taxonomie.TaxonomieCategorie> res = new ArrayList<>();
        //Source,id,label,surcategorie,regroupement,emoji
        Taxonomie.TaxonomieCategorie categorie = null;
        Taxonomie.TaxonomieCategorie.TaxonomieElement element = null;
        //ordre issu de la source
        for (Map<String, String> g : lignesCsv) {
            //une ligne = 1 atome, sauf les lignes blanches qui terminent un groupe
            String atomeKey = g.getOrDefault("id", "");
            String atomeLabel = g.getOrDefault("label", "");
            String categorieLabel = g.getOrDefault("regroupement", "").replace("je veux", "");
            String elementId = g.getOrDefault("idMPS", "");
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
                categorie = new Taxonomie.TaxonomieCategorie(categorieLabel, emoji);
                res.add(categorie);
            }
            if(categorieLabel.isBlank() && atomeKey.isBlank() ) {
                categorie = null;
                element = null;
                continue;
            }
            if(!elementId.isEmpty()) {
                if (categorie == null) {
                    throw new RuntimeException("Pas de catégorie pour l'élément " + elementLabel);
                }
                if (emoji.isBlank()) {
                    throw new RuntimeException(" Element " + elementLabel + "sans emoji");
                }
                if (element != null && element.atomes().isEmpty()) {
                    throw new RuntimeException("Element sans atome " + element.label());
                }
                if(elementLabel.isBlank()) {
                    throw new RuntimeException("Element sans label " + elementId);
                }
                element = Taxonomie.TaxonomieCategorie.TaxonomieElement.build(
                        elementId,
                        new HashMap<>(),
                        elementLabel,
                        emoji,
                        ""
                );
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
