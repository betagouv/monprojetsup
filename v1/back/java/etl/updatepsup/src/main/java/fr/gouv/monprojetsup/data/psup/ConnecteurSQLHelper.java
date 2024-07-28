package fr.gouv.monprojetsup.data.psup;

import oracle.jdbc.pool.OracleDataSource;

import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class ConnecteurSQLHelper {
	private static final Logger LOGGER = Logger.getLogger(ConnecteurSQLHelper.class.getSimpleName());
    public static ConnecteurSQL getConnecteur(ConnectionParams params) throws ConnecteurSQL.AccesDonneesException {
        if(params.disableFan) {
            try {
                OracleDataSource ods = new OracleDataSource();
                ods.setURL(params.url);
                ods.setUser(params.user);
                ods.setPassword(params.password);
                Properties p = new Properties();
                p.setProperty("oracle.jdbc.fanEnabled", "FALSE");
                ods.setConnectionProperties(p);
                LOGGER.info("*** CONNEXION  BDD : User "+params.user);
                LOGGER.info("*** CONNEXION  BDD : url "+params.url);
                
                return new ConnecteurSQL(ods.getConnection());
            } catch (SQLException e) {
                throw new ConnecteurSQL.AccesDonneesException(AccesDonneesExceptionMessage.MESSAGE, e, e.getMessage());
            }

        } else {
            LOGGER.info("*** CONNEXION  BDD : User "+params.user);
            LOGGER.info("*** CONNEXION  BDD : url"+params.url);
            return new ConnecteurSQL(
                    params.url,
                    params.user,
                    params.password
            );
        }
    }
}
