package fr.gouv.monprojetsup.data.model.stats;

import java.io.Serializable;

public record AdmisMatiereBacAnnee(int annLycee, String iMtCod, String bac, int nb) implements Serializable {

    @Override
    public String toString() {
        return "AdmisMatiereBacAnnee[" +
                "annLycee=" + annLycee + ", " +
                "mat=" + iMtCod + ", " +
                "bac=" + bac + ", " +
                "nb=" + nb + ']';
    }


}
