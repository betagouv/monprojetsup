package fr.gouv.monprojetsup.data.model.rome;


import fr.gouv.monprojetsup.data.Constants;

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

        public String getKey() {
            return Constants.CENTRE_INTERETS_ROME + Math.abs(libelle_centre_interet().hashCode());
        }
    }

}
