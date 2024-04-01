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
package fr.gouv.monprojetsup.data.update.psup;

import oracle.jdbc.pool.OracleDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnecteurSQL implements AutoCloseable {

    public static final String FOR_INSCRIPTIONS_TABLE = " G_TRI_INS ";
    public static final String RECRUTEMENTS_GROUPES_TABLE = " A_REC_GRP ";
    public static final String RECRUTEMENTS_GROUPES_TABLE_ANNEE_PREC = " A_REC_GRP_ARCH ";
    public static final String RECRUTEMENTS_FORMATIONS_TABLE = " A_REC ";
    public static final String CLASSEMENTS_TABLE = " C_CAN_GRP ";
    public static final String CLASSEMENTS_TABLE_ANNEE_PREC = " C_CAN_GRP_ARCH ";
    public static final String CLASSEMENTS_INTERNATS_TABLE = " C_CAN_GRP_INT ";
    public static final String CANDIDATS_TABLE = " G_CAN ";
    public static final String INSCRIPTIONS_TABLE = " I_INS ";
    public static final String VOEUX_TABLE = " A_VOE ";
    public static final String STATUTS_VOEUX_TABLE = " A_SIT_VOE ";
    public static final String ADMISSIONS_TABLE = " A_ADM ";
    public static final String ADMISSIONS_TABLE_ANNEE_PREC = " A_ADM_ARCH ";
    public static final String ADMISSIONS_TABLE_SORTIE = " A_ADM_PROP ";
    public static final String FOR_TYPE_TABLE = " G_FOR " ;
    public static final String FIL_TYPE_TABLE = " G_FIL " ;
    public static final String C_GRP = " C_GRP " ;
    public static final String C_JUR_ADM = " C_JUR_ADM " ;
    public static final String J_ORD_APPEL_TMP = " J_ORD_APPEL_TMP " ;
    public static final String G_PAR = " G_PAR " ;
    public static final String A_ADM_DEM = " A_ADM_DEM ";
    public static final int A_AD_TYP_DEM_RA = 1;
    public static final int A_AD_TYP_DEM_GDD = 2;
    public static final String A_ADM_PRED_DER_APP = " A_ADM_PRED_DER_APP ";
    public static final String A_REC_GRP_INT_PROP = " A_REC_GRP_INT_PROP ";
    public static final String A_VOE_PROP = " A_VOE_PROP ";
    public static final String A_REC_GRP_INT = " A_REC_GRP_INT ";
    public static final String V_PROP_RAN_DER_APP = "V_PROP_RAN_DER_APP";
    public static final String V_PROP_REC_GRP = "V_PROP_REC_GRP";
    public static final String V_PROP_REC_GRP_INT = "V_PROP_REC_GRP_INT";
    public static final String V_PROP_CAN_RA = "V_PROP_CAN_RA";

    public static final String V_PROP_PROP = "V_PROP_PROP";

    public static final String V_PROP_ADM = "V_PROP_ADM";

    public static final String V_PROP_VOE = "V_PROP_VOE";

    public static final String V_PROP_VOE_INT = "V_PROP_VOE_INT";

    public static final String V_PROP_ATT_PROP_ANT = "V_PROP_ATT_PROP_ANT";

    /* connexion à la base de données */
    private final Connection conn;

    /* spécifie si la connexion doit être close en même temps que l'objet */
    private final boolean cleanupOnClose;

    public ConnecteurSQL(Connection connection) throws AccesDonneesException {
        if (connection == null) {
            throw new AccesDonneesException(AccesDonneesExceptionMessage.CONNECTEUR_SQL_CONNEXION_NULL);
        }
        cleanupOnClose = false;
        this.conn = connection;
    }

    public ConnecteurSQL(String url, String user, String password) throws AccesDonneesException {
        cleanupOnClose = true;
        try {
        	if(url.startsWith("jdbc:oracle:thin:@")) {
        		OracleDataSource ods = new OracleDataSource();
    			ods.setURL(url);
    			ods.setUser(user);
    			ods.setPassword(password);
    			conn=ods.getConnection();
        	}else {
            conn = DriverManager.getConnection(url, user, password);
        	}
        } catch (SQLException ex) {
            throw new AccesDonneesException(AccesDonneesExceptionMessage.CONNECTEUR_SQL_CREATION, ex);
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

        private static final long serialVersionUID = 1L;

        public AccesDonneesException(AccesDonneesExceptionMessage exceptionMessage, Object... arguments) {
            super(String.format(exceptionMessage.getMessage(), arguments));
        }

        public AccesDonneesException(AccesDonneesExceptionMessage exceptionMessage, Throwable cause, Object... arguments) {
            super(String.format(exceptionMessage.getMessage(), arguments), cause);
        }

    }
}
