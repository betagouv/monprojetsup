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
public class Filiere implements Serializable {

    public static final int MIN_LENGTH_FOR_FIL_KEYWORD = 3;

    /* libellé (champ gFlLib en base). */
    public final String libelle;

    /* sigle */
    public final String sigle;

    /* code unique identifiant uniquement la filière dans la base */
    public final int cle;

    /* deux versions pour certaines filières: avec ou sans apprentissage */
    public final boolean apprentissage;

    public final boolean isLas;

    /* code identifiant la filière san,s apprentissage dans la base */
    public final int cleFiliere;

    public final Set<String> motsClesParcoursup = new HashSet<>();

    public final Set<String> motsClesOnisepv2 = new HashSet<>();

    public Filiere(String libelle,
                   String sigle,
                   int cle, int cleFiliere,
                   boolean apprentissage,
                   boolean isLas) {
        this.libelle = libelle;
        this.sigle = sigle;
        this.cle = cle;
        this.cleFiliere = cleFiliere;
        this.apprentissage = apprentissage;
        this.isLas = isLas;
        motsClesParcoursup.add(libelle);
        motsClesParcoursup.add(sigle);
        motsClesParcoursup.addAll(Arrays.asList(sigle.replace('/', ' ').replace('-', ' ').split(" ")));
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
