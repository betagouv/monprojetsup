package fr.gouv.monprojetsup.data.psup;


import java.util.logging.Logger;

public class ConnecteurSQLHelper {
	private static final Logger LOGGER = Logger.getLogger(ConnecteurSQLHelper.class.getSimpleName());
    public static ConnecteurSQL getConnecteur(ConnectionParams params) throws ConnecteurSQL.AccesDonneesException {
        LOGGER.info("*** CONNEXION  BDD : User "+params.user);
        LOGGER.info("*** CONNEXION  BDD : url"+params.url);
        return new ConnecteurSQL(
                params.url,
                params.user,
                params.password
        );
    }
}
