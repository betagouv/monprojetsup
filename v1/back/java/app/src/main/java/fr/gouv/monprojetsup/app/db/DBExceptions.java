package fr.gouv.monprojetsup.app.db;

public class DBExceptions {

    public static class DBException extends java.lang.Exception {
        public DBException(String msg) {
            super(msg);
        }
    }

    private DBExceptions() {
    }


    public static class ModelException extends DBException {
        public ModelException(String msg) {
            super(msg);
        }
    }

    public static class UnknownUserException extends ModelException {
        public UnknownUserException(String login) {
            super("Login '" + login + "' inconnu.");
        }
    }

    public static class NotFoundException extends ModelException {
        public NotFoundException(String id, String collectionName) {
            super("No item with id '" + id + "' in collection '" + collectionName + "'.");
        }
    }


    public static class InvalidTokenException extends UserInputException {

        public InvalidTokenException(String details) {
            super("Jeton d'authentification erroné: " + details);
        }
    }


    public static class ServerStartingException extends UserInputException {

        public ServerStartingException() {
            super("Service en cours de démarrage, veuillez réessayer dans quelques secondes");
        }

    }

    public static class UnknownGroupException extends ModelException {
        public UnknownGroupException() {
            super("Groupe inconnu.");
        }
    }


    public static class EmptyGroupIdException extends ModelException {
        public EmptyGroupIdException() {
            super("Le nom d'un groupe doit être non vide.");
        }
    }

    public static class EmptyUserNameException extends ModelException {
        public EmptyUserNameException() {
            super("Le nom d'un utilisateur doit être non vide.");
        }
    }


    public static class UserInputException extends DBException {
        public UserInputException(String msg) {
            super(msg);
        }


        public static class JwtExpired extends UserInputException {
            public JwtExpired() {
                super("Le jeton d'authentification a expiré.");
            }
        }

        public static class JwtWrongIssuer extends UserInputException {
            public JwtWrongIssuer() {
                super("Le jeton d'authentification n'a pas le bon émetteur.");
            }
        }

        public static class JwtWrongData extends UserInputException {
            public JwtWrongData() {
                super("Le jeton d'authentification ne contient pas de données.");
            }
        }

        public static class InvalidPasswordException extends UserInputException {
            public InvalidPasswordException() {
                super("Pas de compte avec cet identifiant ou mot de passe erroné.");
            }
        }

        public static class EmailNotVerifiedFromOidcCreationException extends UserInputException {
            public EmailNotVerifiedFromOidcCreationException() {
                super("Votre email n'est pas vérifié, par conséquent la création automatique du compte n'est pas possible, merci de créer un compte manuellement.");
            }
        }

        public static class AccountCreationFromOidcNotAvailableYet extends UserInputException {
            public AccountCreationFromOidcNotAvailableYet() {
                super("La création automatique du compte via OIDC n'est pas encore disponible, merci de créer un compte manuellement.");
            }
        }

        public static class WrongAccessCodeException extends UserInputException {
            public WrongAccessCodeException() {
                super("Code d'accès incorrect.");
            }
        }

        public static class InvalidGroupTokenException extends UserInputException {
            public InvalidGroupTokenException() {
                super("Code d'accès au groupe incorrect.");
            }
        }

        public static class DoubleAuthException extends UserInputException {

            public DoubleAuthException(String login) {
                super("Double authentification de '" + login + "'.");
            }

        }

        public static class TooManyEmails extends UserInputException {

            public TooManyEmails() {
                super("Veuillez attendre une minute avant l'envoi du prochain email");
            }

        }

        public static class UserAlreadyEnabledException extends UserInputException {

            public UserAlreadyEnabledException() {
                super("Ce compte est déjà activé.");
            }

        }

        public static class UnauthorizedLoginException extends UserInputException {
            public UnauthorizedLoginException() {
                super("Les logins autorisés ne comportent que des caractères alphanumériques et les symboles '+_.-@'");
            }
        }

        public static class UserAlreadyExistsException extends UserInputException {

            public UserAlreadyExistsException() {
                super("Il existe déjà un compte utilisateur avec ce login.");
            }

        }

        public static class MaintenanceException extends UserInputException {

            public MaintenanceException() {
                super("Maintenance en cours, service suspendu.");
            }

        }

        public static class ForbiddenException extends UserInputException {

            public ForbiddenException() { super("Vous n'avez pas les droits suffisants pour effectuer cette opération."); }

        }
    }


}
