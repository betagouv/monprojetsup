package fr.gouv.monprojetsup.data.domain.model.rome;


import java.util.ArrayList;
import java.util.List;

public record InteretsRome(List<Item> arbo_centre_interet) {

    public record Metier(
            int code_ogr_rome,
            String code_rome,
            String libelle_rome) {

    }

    public record Item(
            String libelle_centre_interet,
            List<Metier> liste_metier
    ) {

        public Item() {
            this("", new ArrayList<>());
        }

    }

    public InteretsRome() {
        this(new ArrayList<>());
    }

}
