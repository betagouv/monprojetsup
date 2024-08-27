package fr.gouv.monprojetsup.data.carte.algos.explicabilite;


import fr.gouv.monprojetsup.data.carte.algos.explicabilite.tauxacces.TauxAccesDetails;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class AlgoCarteDetails implements Serializable {

    /* détails sur le calcul du taux d'accès, classé par type de bac */
    public final Map<Integer, TauxAccesDetails> parBac = new TreeMap<>();

}
