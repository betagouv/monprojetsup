package fr.gouv.monprojetsup.suggestions.server;


public class Exceptions {

    public static class UserInputException extends Exception {
        public UserInputException(String msg) {
            super(msg);
        }
    }

    public static class  ServerStartingException extends Exception {

    }

}
