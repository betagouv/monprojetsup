package fr.gouv.monprojetsup.data.psup.exceptions;

import lombok.Getter;

@Getter
public enum AccesDonneesExceptionMessage {
    MESSAGE("%s"),

    CONNECTEUR_ORACLE_CONNEXION_NULL("Impossible de créer un ConnecteurOracle à partir d'une connexion null"),

    CONNECTEUR_DONNEES_APPEL_ORACLE_ERREUR_SQL_RECUPERATION("Erreur SQL lors de la récupération des données d'appel");

    private final String message;

    AccesDonneesExceptionMessage(String message){
        this.message = message;
    }

}
