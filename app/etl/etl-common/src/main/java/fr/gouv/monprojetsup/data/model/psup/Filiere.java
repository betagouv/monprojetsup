/* Copyright 2019 © Ministère de l'Enseignement Supérieur, de la Recherche et de
l'Innovation,
    Hugo Gimbert (hugo.gimbert@enseignementsup.gouv.fr)

    This file is part of Algorithmes-de-parcoursup.

    Algorithmes-de-parcoursup is free software: you can redistribute it and/or modify
    it under the terms of the Affero GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Algorithmes-de-parcoursup is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    Affero GNU General Public License for more details.

    You should have received a copy of the Affero GNU General Public License
    along with Algorithmes-de-parcoursup.  If not, see <http://www.gnu.org/licenses/>.

 */
package fr.gouv.monprojetsup.data.model.psup;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* 
    Une filière de Parcoursup.
    Exemple: "DUT - Production/Mesures physiques"
 */
public record Filiere (
        /* libellé (champ gFlLib en base). */
        String libelle,

        /* sigle */
        String sigle,

        /* code unique identifiant uniquement la filière dans la base */
        int cle,

        /* deux versions pour certaines filières: avec ou sans apprentissage */
        boolean apprentissage,

        boolean isLas,

        /* code identifiant la filière san,s apprentissage dans la base */
        int cleFiliere,

        Set<String> motsClesParcoursup,

        Set<String> motsClesOnisepv2

        ) implements Serializable {

    public static final int MIN_LENGTH_FOR_FIL_KEYWORD = 3;


    public Filiere(String libelle,
                   String sigle,
                   int cle, int cleFiliere,
                   boolean apprentissage,
                   boolean isLas) {
        this(libelle, sigle, cle, apprentissage, isLas, cleFiliere, new HashSet<>() , new HashSet<>());
        motsClesParcoursup.add(libelle);
        motsClesParcoursup.add(sigle);
        motsClesParcoursup.addAll(Arrays.asList(sigle.replace('/', ' ').replace('-', ' ').split(" ")));
    }

    //used by Jackson
    private Filiere() {
        this("", "", 0, false, false, 0, new HashSet<>() , new HashSet<>());
    }

    @Override
    public String toString() {
        return sigle + " - " + libelle;
    }

    public void addMotCle(String chain) {
        if(chain.length() >= MIN_LENGTH_FOR_FIL_KEYWORD) {
            motsClesParcoursup.add(chain);
        }
    }
    public void addMotCles(List<String> chain) {
        chain.forEach(this::addMotCle);
    }

}
