package fr.gouv.monprojetsup.suggestions.infrastructure.model.stats;

import java.io.Serializable;

public record AdmisMatiereBacAnnee(int annLycee, int iMtCod, String bac, int nb) implements Serializable {

    @Override
    public String toString() {
        return "AdmisMatiereBacAnnee[" +
                "annLycee=" + annLycee + ", " +
                "iMtCod=" + iMtCod + ", " +
                "bac=" + bac + ", " +
                "nb=" + nb + ']';
    }


}
