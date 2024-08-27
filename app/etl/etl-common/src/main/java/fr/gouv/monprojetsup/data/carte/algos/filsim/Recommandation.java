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
package fr.gouv.monprojetsup.data.carte.algos.filsim;

import fr.gouv.monprojetsup.data.carte.algos.Filiere;

import java.util.Map;
import java.util.TreeMap;

/* 
    Une recommandation.
 */
public class Recommandation {

    private static final int MAX_CORRELATION = 100;
    public static final int MAX_PROXIMITE_SEMANTIQUE = 10000;

    public final Filiere filiere2;

    public final int similarite;

    public final int correlation;

    public final int proximiteSemantique;

    public final Map<String,Integer> motsClesProximite = new TreeMap<>();


    Recommandation(
            Filiere filiere1,
            Filiere filiere2,
            int proximiteSemantique,
            double corr,
            Map<String,Integer> motsClesProximite) {

        this.filiere2 = filiere2;

        this.motsClesProximite.putAll(motsClesProximite);

        if (filiere1.cle == filiere2.cle) {
            this.proximiteSemantique = MAX_PROXIMITE_SEMANTIQUE;
            this.correlation = MAX_CORRELATION;
            this.similarite = MAX_PROXIMITE_SEMANTIQUE * MAX_CORRELATION;
        } else {


            this.correlation =  (int) (MAX_CORRELATION * corr);

            this.proximiteSemantique = proximiteSemantique;

            if(correlation == 0 && proximiteSemantique == 0) {
                similarite = 0;
            } else {
                similarite = Math.max(1,correlation) * Math.max(1, proximiteSemantique);
            }
        }
    }

}
