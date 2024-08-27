package fr.gouv.monprojetsup.data.carte.algos.explicabilite.tauxacces;

import java.io.Serializable;

public class TauxAccesDetailsGroupe implements Serializable {

    @SuppressWarnings("unused")
    public int rangDernierAppele;

    public int nbCanAuDessusBarre;

    public int nbCandidats;

    public TauxAccesDetailsGroupe() {
        rangDernierAppele = -1;
        nbCanAuDessusBarre = 0;
        nbCandidats = 0;
    }

}
