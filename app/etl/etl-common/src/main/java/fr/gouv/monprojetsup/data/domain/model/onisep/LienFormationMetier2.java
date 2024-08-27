package fr.gouv.monprojetsup.data.domain.model.onisep;

import java.util.ArrayList;
import java.util.List;

public record LienFormationMetier2(
        List<FormationMetierPaireOnisep> paires
) {

    public record FormationMetierPaireOnisep(
            String filiere,
            String metier,
            List<String> metiers,

            String domaine
    ) {

        public List<String> metiersList(FichesMetierOnisep fiches) {
            List<String> l = new ArrayList<>();
            if(metier !=null) l.add(metier);
            if(metiers !=null) l.addAll(metiers);
            if(domaine != null) {
                l.addAll(fiches.getMetiersOfDomain(domaine));
            }
            return l;
        }
    }
}
