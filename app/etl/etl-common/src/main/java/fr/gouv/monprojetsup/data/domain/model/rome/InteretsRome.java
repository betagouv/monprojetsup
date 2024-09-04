package fr.gouv.monprojetsup.data.domain.model.rome;


import fr.gouv.monprojetsup.data.domain.Constants;

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

        public String getKey() {
            return Constants.CENTRE_INTERETS_ROME + Math.abs(libelle_centre_interet().hashCode());
        }
    }

    public InteretsRome() {
        this(new ArrayList<>());
    }

}
