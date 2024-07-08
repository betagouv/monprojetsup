package fr.gouv.monprojetsup.app.server;

public class ServerStartingException extends RuntimeException {

    public ServerStartingException() {
        super("Service en cours de démarrage, veuillez réessayer dans quelques secondes");
    }

}
