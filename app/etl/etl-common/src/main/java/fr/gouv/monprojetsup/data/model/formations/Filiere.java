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

/**
 *
 * @author gimbert
 */
public record Filiere
(
    /* le code de la filière */
    int gFlCod,
    /* le groupe de formations auquel appartient la filière */
    int gFrCod,
    String libelle,
    boolean apprentissage,
    int gFlCodeFi
) implements Serializable{
    
    @Override
    public String toString() {
        return "Filière '" + libelle + " (" + gFlCod + ")"; 
    }

    public boolean isL1() {
        return libelle.contains("L1") || libelle.contains("Licence");
    }

    public boolean isCUPGE() {
        return libelle.contains("CUPGE");
    }

    public boolean isLouvre() {
        return gFlCod == 250001;
    }

    @SuppressWarnings("unused")
    private Filiere() {
        this(Integer.MIN_VALUE,Integer.MIN_VALUE,null,false,Integer.MIN_VALUE);
    }

}
