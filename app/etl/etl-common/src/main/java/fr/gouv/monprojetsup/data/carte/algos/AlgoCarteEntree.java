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
package fr.gouv.monprojetsup.data.carte.algos;


import fr.gouv.monprojetsup.data.carte.algos.tags.FormationCarteAlgoTags;

import java.io.Serializable;
import java.util.*;


/* données d'entrées pour les algos de la carte */
public class AlgoCarteEntree implements Serializable {

    /* les filières de Parcoursup, indexées par gFlCod */
    public final Map<Integer, Filiere> filieres = new HashMap<>();

    /* les formations, indexées par gTaCod */
    public final Map<Integer, FormationCarteAlgoTags> formations = new HashMap<>();

    /* les taux d'accès entre 0 et 100 indexés par 1) type de bac 2) gTaCod */
    public final transient Map<Integer, Map<Integer,Integer>> tauxAcces = new HashMap<>();

    /* associe à chaque candidat la liste des filieres qu il a sélectionné */
    public final Map< Integer, Set<Integer>> candidatsParFiliere = new HashMap<>();

    /* associe à chaque candidat son type de bac */
    public final Map< Integer, Integer> typesBacCandiats = new HashMap<>();

    public Filiere getFiliere(int gFlCod, boolean isLas) {
        if(!isLas) return filieres.get(gFlCod);
        else return filieres.get(Filiere.LAS_CONSTANT + gFlCod);
    }


}

