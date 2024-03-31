/* Copyright 2019 © Ministère de l'Enseignement Supérieur, de la Recherche et de l'Innovation,
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
package fr.gouv.monprojetsup.data.update.psup;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

@XmlRootElement
public class ConnectionParams implements Serializable {

    public  String url;
    public final String tnsAlias;
    public String user;
    public String password;
    public final boolean disableFan;

    public ConnectionParams() {
        url = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=127.0.0.1)(PORT=1234))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=MON_SERVICE)))";
        tnsAlias = "my_TNS";
        user = "my_login";
        password = "my_pwd";
        disableFan = false;
    }

}
