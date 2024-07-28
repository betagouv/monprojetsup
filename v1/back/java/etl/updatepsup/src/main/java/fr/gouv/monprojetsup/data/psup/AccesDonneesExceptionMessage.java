package fr.gouv.monprojetsup.data.psup;

public enum AccesDonneesExceptionMessage {
    MESSAGE("%s"),

    CONNECTEUR_SQL_CONNEXION_NULL("Impossible de créer un ConnecteurSQL à partir d'une connexion null"),
    CONNECTEUR_SQL_CREATION("Echec de création du connecteur SQL"),

    SERIALISATION_ENTREE_SORTIE("Erreur d'entrée-sortie"),
    SERIALISATION_SERIALISATION("Erreur de sérialisation"),

    CONNECTEUR_DONNEES_APPEL_SQL_GROUPE_MANQUANT("Pas de groupe avec le C_GP_COD %s"),
    CONNECTEUR_DONNEES_APPEL_SQL_ERREUR_SQL_RECUPERATION("Erreur SQL lors de la récupération des données d'appel"),
    CONNECTEUR_DONNEES_APPEL_SQL_INTEGRITE("Problème d'intégrité des données d'appel"),
    CONNECTEUR_DONNEES_APPEL_SQL_EXPORTATION("Erreur SQL lors de l'exportation des données"),

    CALCUL_ORDRE_APPEL_PROD_TNS_ADMIN("La variable d'environnement TNS_ADMIN n'est pas positionnée"),

    ENVOI_PROPOSITIONS_PROD_TNS_ADMIN("La variable d'environnement TNS_ADMIN n'est pas positionnée"),

    CONNECTEUR_DONNEES_PROPOSITIONS_SQL_RECUPERATION("Echec de la recuperation des données"),
    CONNECTEUR_DONNEES_PROPOSITIONS_SQL_EXPORT("Echec de l'export des propositions"),
    CONNECTEUR_DONNEES_PROPOSITIONS_SQL_DATE("Date incohérente"),

    CONNECTEUR_DONNEES_PROPOSITIONS_SQL_DATE_INCONNUE("Date inconnue, g_pr_cod=%s"),
    CONNECTEUR_DONNEES_PROPOSITIONS_SQL_ENTREE("Problème d'intégrité des données d'entrée"),
    CONNECTEUR_DONNEES_PROPOSITIONS_SQL_MAUVAIS_G_PR_COD("Veuillez interrompre le flux de données entrantes et positionner le g_pr_cod=%s à 1"),

    CONNECTEUR_DONNEES_PROPOSITIONS_XML_DESERIALISATION("Erreur de deserialisation des donnees d'entree")

    ;

    private final String message;

    AccesDonneesExceptionMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}
