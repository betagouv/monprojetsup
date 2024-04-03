/* Copyright 2020 © Ministère de l'Enseignement Supérieur, de la Recherche et de
l'Innovation,
    Hugo Gimbert (hugo.gimbert@enseignementsup.gouv.fr)

    This file is part of orientation-parcoursup.

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

package fr.gouv.monprojetsup.data.update.psup;


import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;


@XmlRootElement
public class DatabaseConfig implements Serializable {

    /* une base dans laquelles les Neo ont leur notes de controle continu et de bac anticipées,
     mais pas bac final, telles que portées à la connaissance des CEV */
    public final ConnectionParams dbNotes;

    /* une base avec tous les voeux de PP, la veille du démarrage des adamissions */
    public final ConnectionParams dbVoeux;

    /* une base dans lesquelles les Neo ont leurs admissions en PP */
    public final ConnectionParams dbAdmissions;


    public DatabaseConfig() {
        dbNotes = new ConnectionParams();
        dbVoeux = new ConnectionParams();
        dbAdmissions = new ConnectionParams();
    }


}
