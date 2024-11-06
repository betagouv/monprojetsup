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
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author gimbert
 */
public class Formations  implements Serializable {

    /* indexed by gFrCod */
    public final Map<Integer, String> typesMacros = new HashMap<>();

    /* indexed by gFlCod */
    public final Map<Integer, Filiere> filieres = new HashMap<>();

    /* indexed by gTaCod */
    public final Map<Integer, Formation> formations = new HashMap<>();
    
    public void creerFiliere(int gFlCod, int gFrCod, String libelle, boolean apprentissage, int gFlCodFi) {
        filieres.put(gFlCod, new Filiere(gFlCod, gFrCod, libelle, apprentissage,gFlCodFi));
    }

    public Filiere getFiliere(int gFlCod) {
        Filiere resultat = filieres.get(gFlCod);
        if (resultat == null) {
            throw new IllegalArgumentException("Filiere introuvable");
        }
        return resultat;
    }

    public boolean hasFiliere(int gFlCod) {
        return filieres.containsKey(gFlCod);
    }

    public void clear() {
        filieres.clear();
    }

    public Collection<Filiere> getFilieres() {
        List<Filiere> result = new ArrayList<>(filieres.values());
        result.sort(Comparator.comparingInt(Filiere::gFlCod));
        return result;
    }

    final Map<Integer, Filiere> _groupesToFilieres = new HashMap<>();


    public void ajouterSiInconnu(Formations o) {
        o.formations.forEach((key, value) -> {
                this.formations.putIfAbsent(key, value);
        });
        o.filieres.forEach(this.filieres::putIfAbsent);
        o.typesMacros.forEach(this.typesMacros::putIfAbsent);
        _groupesToFilieres.clear();

        cleanup();
    }

    public void cleanup() {

        formations.values().removeIf(f -> !filieres.containsKey(f.gFlCod));
        formations.values().removeIf(f -> f.capacite <= 0);

        Set<Integer> filieresActives = formations.values().stream().map(f -> f.gFlCod).collect(Collectors.toSet());
        filieres.keySet().retainAll(filieresActives);

        Set<Integer> typesMacrosActifs = filieres.values().stream().map(Filiere::gFrCod).collect(Collectors.toSet());
        typesMacros.keySet().retainAll(typesMacrosActifs);

    }
}
