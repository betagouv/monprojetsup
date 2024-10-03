package fr.gouv.monprojetsup.data.etl.loaders;

import fr.gouv.monprojetsup.data.model.taxonomie.Taxonomie;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;

public class DomainesMpsLoader {


    private DomainesMpsLoader() {
    }

    public static List<Taxonomie.TaxonomieCategorie> getTaxonomieCategories(
            List<Map<String, String>> lignesCsv,
            Map<String, String> labelsAtomes
    ) {
        List<Taxonomie.TaxonomieCategorie> res = new ArrayList<>();
        Taxonomie.TaxonomieCategorie categorie = null;

        for (Map<String, String> g : lignesCsv) {
            String labelCategorie = g.getOrDefault("regroupements MPS", "");
            String idElement = g.getOrDefault("cle domaine MPS", "");
            String labelElement = g.getOrDefault("domaines MPS", "");
            String emojiCategorie = g.getOrDefault("emoji regroupements MPS", "");
            String emojiElement = g.getOrDefault("emoji domaines MPS", "");
            String atomesList = g.getOrDefault("clés ideo2 des sousdomaines web", "");

            if(!labelCategorie.isBlank()) {
                if(emojiCategorie.isBlank()) {
                    throw new RuntimeException(" Regroupement " + labelCategorie + "sans emoji");
                }
                categorie = new Taxonomie.TaxonomieCategorie(labelCategorie, emojiCategorie);
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
            val atomes = Arrays.stream(atomesList.split(";"))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.toMap(
                            a -> a,
                            labelsAtomes::get
                    ));
            if(atomes.isEmpty()) {
                throw new RuntimeException("Element sans atome");
            }
            if(atomes.containsValue(null)) {
                throw new RuntimeException("Atome avec code inconnu:" + atomes);
            }

            val description = g.getOrDefault("descriptif", "");
            val element = Taxonomie.TaxonomieCategorie.TaxonomieElement.build(
                    idElement,
                    atomes,
                    labelElement,
                    emojiElement,
                    description
            );
            categorie.elements().add(element);
        }
        return res;
    }

}
