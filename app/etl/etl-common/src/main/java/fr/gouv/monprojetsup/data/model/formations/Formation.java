/* Copyright 2020 © Ministère de l'Enseignement Supérieur, de la Recherche et de
l'Innovation,
    Hugo Gimbert (hugo.gimbert@enseignementsup.gouv.fr)

    This file is part of parcoursup-simulations.

    parcoursup-simulations is free software: you can redistribute it and/or modify
    it under the terms of the Affero GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    parcoursup-simulations is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    Affero GNU General Public License for more details.

    You should have received a copy of the Affero GNU General Public License
    along with parcoursup-simulations.  If not, see <http://www.gnu.org/licenses/>.

 */
package fr.gouv.monprojetsup.data.model.formations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gimbert
 */
public class Formation implements Serializable {

    public final int gTaCod;

    public final int gTiCod;

    public final int gFlCod;

    public final String libelle;

    public final String academie;

    public final int academieCode;

    public final int capacite;

    public final String etablissement;

    public final List<Integer> groupes = new ArrayList<>();

    /* geodetic coordinates */
    public final Double lat;
    public final Double lng;

    public final String commune;

    public final String codeCommune;

    public Formation(
            int gTaCod,
            int gTiCod,
            String libelle,
            int gFlCod,
            String academie,
            String gEaCodAff,
            int academieCode,
            int capacite,
            Double lat,
            Double lng,
            String commune,
            String codeCommune) {
        this.gTaCod = gTaCod;
        this.gTiCod = gTiCod;
        this.gFlCod = gFlCod;
        this.libelle = libelle.replace(";", ",").replace("\n", "");
        this.academie = academie;
        this.etablissement = gEaCodAff;
        this.academieCode = academieCode;
        this.capacite = capacite;
        this.lat = lat;
        this.lng = lng;
        this.commune = commune;
        this.codeCommune = codeCommune;
    }


    @Override
    public String toString() {
        return libelle + "(g_ta_cod=" + gTaCod + ")";
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(gTaCod);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Formation mtb) {
            return (gTaCod == mtb.gTaCod);
        } else {
            throw new ClassCastException("Test d'égalité imprévu");
        }
    }

    private Formation() {
        this.gTaCod = Integer.MIN_VALUE;
        this.gTiCod = Integer.MIN_VALUE;
        this.gFlCod = Integer.MIN_VALUE;
        this.libelle = null;
        this.academie = "";
        this.etablissement = "";
        this.academieCode = 0;
        this.capacite = 0;
        this.lat = null;
        this.lng = null;
        this.commune = "";
        this.codeCommune = "";
    }


    public boolean isIFSI() {
        boolean result = (gFlCod == 550001 || gFlCod == 551001);
        if(result && !libelle.toLowerCase().contains("infirmier")) throw new RuntimeException("isIFSI is not valid anymore");
        return result;
    }

    public boolean isLAS() {
        return libelle.contains("(LAS)");
    }


    public boolean isPPPE() {
        return libelle.contains("PPPE");
    }
}
