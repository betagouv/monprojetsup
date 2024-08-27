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
import fr.gouv.monprojetsup.data.carte.algos.explicabilite.reco.AlgoFilieresSimilairesDetailsCalcul;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class AlgoFilieresSimilairesSortie {

    /* liste de filieres, chacune contenant une liste de mots-clés */    
    public final Collection<Filiere> filieres = new ArrayList<>();
    
    /* liste de recommandations et leur score, par type de bac */
    public final Map<Integer, AlgoFilieresSimilairesDetailsCalcul> recommandations = new HashMap<>();
        
}
