package fr.gouv.monprojetsup.data.psup;

@SuppressWarnings("unused")
public enum AccesDonneesExceptionMessage {
    MESSAGE("%s"),

    CONNECTEUR_ORACLE_CONNEXION_NULL("Impossible de créer un ConnecteurOracle à partir d'une connexion null"),
    CONNECTEUR_ORACLE_CREATION("Echec de création du connecteur"),

    CONNECTEUR_DONNEES_APPEL_ORACLE_ERREUR_SQL_RECUPERATION("Erreur SQL lors de la récupération des données d'appel");

    private final String message;

    AccesDonneesExceptionMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}
