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
package fr.gouv.monprojetsup.suggestions.data.model.formations;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author gimbert
 */
public class Filiere implements Serializable {
    
    public final int gFlCod;

    public final int gFlCodeFi;
    
    /* le groupe de formations auquel appartient la filière */
    public final int gFrCod;

    public final String libelle;

    public final boolean apprentissage;
    
    /* groupes représentés par leur C_GP_COD */
    public final Set<Integer> groupes() { return libellesGroupes.keySet(); }

    public final Map<Integer, String > libellesGroupes = new HashMap<>();

    /* liste de liens vers des anciens g_ta_cod */
    public final Set<Integer> ancienGFlCod = new HashSet<>();

    Filiere(int gFlCod, int gFrCod, String libelle, boolean apprentissage, int gFlCodFi) {
        this.gFlCod = gFlCod;
        this.gFrCod = gFrCod;
        this.libelle = libelle;
        this.apprentissage = apprentissage;
        this.gFlCodeFi = gFlCodFi;
    }
    
    @Override
    public String toString() {
        return "Filière '" + libelle + " (" + gFlCod + ")"; 
    }

    private Filiere() {
        this.gFlCod = Integer.MIN_VALUE;
        this.gFrCod = Integer.MIN_VALUE;
        this.libelle = null;
        apprentissage = false;
        this.gFlCodeFi = Integer.MIN_VALUE;
    }



    public boolean hasGroup(int cGpCod) {
        return libellesGroupes.containsKey(cGpCod);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filiere filiere = (Filiere) o;
        return gFlCod == filiere.gFlCod && gFrCod == filiere.gFrCod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gFlCod, gFrCod);
    }

}
