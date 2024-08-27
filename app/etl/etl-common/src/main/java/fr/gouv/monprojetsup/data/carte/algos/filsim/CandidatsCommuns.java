package fr.gouv.monprojetsup.data.carte.algos.filsim;

import jakarta.xml.bind.annotation.XmlTransient;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CandidatsCommuns implements Serializable {
    /* pour chaque autre filière, le nombre de candidats ayant fait un voeu pour
    ces deux filières à la fois */
    @XmlTransient
    private final transient Map<Integer, Long> candidatsCommuns = new HashMap<>();

    public void ajouterFiliereAvecVoeuxCommuns(int filiere2, long nbVoeuxCommuns) {
        long actuel = candidatsCommuns.getOrDefault(filiere2, 0L);
        candidatsCommuns.put(filiere2, actuel + nbVoeuxCommuns);
    }

    /* nombre de candidats à l'année n-1 */
    public long nbCandidatsAnneePrecedente(int f) {
        return candidatsCommuns.getOrDefault(f, 0L);
    }

    public boolean contains(Integer fil2) {
        return candidatsCommuns.containsKey(fil2);
    }

}
