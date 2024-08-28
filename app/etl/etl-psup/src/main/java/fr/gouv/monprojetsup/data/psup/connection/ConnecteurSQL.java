/* Copyright 2019 © Ministère de l'Enseignement Supérieur, de la Recherche et de l'Innovation,
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
package fr.gouv.monprojetsup.data.psup.connection;


import fr.gouv.monprojetsup.data.psup.exceptions.AccesDonneesExceptionMessage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnecteurSQL implements AutoCloseable {

    public static final String FOR_TYPE_TABLE = " G_FOR " ;
    public static final String FIL_TYPE_TABLE = " G_FIL " ;

    /* connexion à la base de données */
    private final Connection conn;

    /* spécifie si la connexion doit être close en même temps que l'objet */
    private final boolean cleanupOnClose;

    public ConnecteurSQL(String url, String user, String password) throws AccesDonneesException {
        cleanupOnClose = true;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            throw new AccesDonneesException(AccesDonneesExceptionMessage.CONNECTEUR_ORACLE_CREATION, ex);
        }
    }

    public Connection connection() {
        return conn;
    }

    @Override
    public void close() throws SQLException {
        if (cleanupOnClose && conn != null) {
            conn.close();
        }
    }

    public static class AccesDonneesException extends Exception {

        public AccesDonneesException(AccesDonneesExceptionMessage exceptionMessage, Throwable cause, Object... arguments) {
            super(String.format(exceptionMessage.getMessage(), arguments), cause);
        }

    }
}
