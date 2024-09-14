package fr.gouv.monprojetsup.data.etl.loaders;

import fr.gouv.monprojetsup.data.model.taxonomie.TaxonomieCategorie;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;

public class DomainesMpsLoader {


    private DomainesMpsLoader() {
    }

    public static List<TaxonomieCategorie> getTaxonomieCategories(
            List<Map<String, String>> lignesCsv,
            Map<String, String> labelsAtomes
    ) {
        List<TaxonomieCategorie> res = new ArrayList<>();
        TaxonomieCategorie categorie = null;

        for (Map<String, String> g : lignesCsv) {
            String labelCategorie = g.getOrDefault("regroupement", "");
            String labelElement = g.getOrDefault("domaines MPS", "");
            String emojiCategorie = g.getOrDefault("emoji regroupement", "");
            String emojiElement = g.getOrDefault("emoji", "");
            String atomesList = g.getOrDefault("clés ideo2 des sousdomaines web", "");

            if(!labelCategorie.isBlank()) {
                if(emojiCategorie.isBlank()) {
                    throw new RuntimeException(" Regroupement " + labelCategorie + "sans emoji");
                }
                categorie = new TaxonomieCategorie(labelCategorie, emojiCategorie, new ArrayList<>());
                res.add(categorie);
            }
            if(categorie == null) {
                throw new RuntimeException("Regroupement sans nom");
            }
            if(labelElement.isBlank()) {
                throw new RuntimeException("Element sans nom");
            }
            if(emojiElement.isBlank()) {
                throw new RuntimeException("Element sans emoji: " + labelElement);
            }
            val atomes = Arrays.stream(atomesList.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.toMap(
                            a -> a,
                            a -> labelsAtomes.getOrDefault(a,a)
                    ));
            if(atomes.isEmpty()) {
                throw new RuntimeException("Element sans atome");
            }
            val element = new TaxonomieCategorie.TaxonomieElement(atomes, labelElement, emojiElement);
            categorie.elements().add(element);
        }
        return res;
    }

}
