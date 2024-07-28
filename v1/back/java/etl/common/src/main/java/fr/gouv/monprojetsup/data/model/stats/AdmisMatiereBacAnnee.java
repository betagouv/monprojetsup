package fr.gouv.monprojetsup.data.model.stats;

import java.io.Serializable;
import java.util.Objects;

public final class AdmisMatiereBacAnnee implements Serializable {
    private final int annLycee;
    private final int iMtCod;
    private final String bac;
    private final int nb;

    public AdmisMatiereBacAnnee(int annLycee, int iMtCod, String bac, int nb) {
        this.annLycee = annLycee;
        this.iMtCod = iMtCod;
        this.bac = bac;
        this.nb = nb;
    }

    public int annLycee() {
        return annLycee;
    }

    public int iMtCod() {
        return iMtCod;
    }

    public String bac() {
        return bac;
    }

    public int nb() {
        return nb;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AdmisMatiereBacAnnee) obj;
        return this.annLycee == that.annLycee &&
                this.iMtCod == that.iMtCod &&
                Objects.equals(this.bac, that.bac) &&
                this.nb == that.nb;
    }

    @Override
    public int hashCode() {
        return Objects.hash(annLycee, iMtCod, bac, nb);
    }

    @Override
    public String toString() {
        return "AdmisMatiereBacAnnee[" +
                "annLycee=" + annLycee + ", " +
                "iMtCod=" + iMtCod + ", " +
                "bac=" + bac + ", " +
                "nb=" + nb + ']';
    }


}
