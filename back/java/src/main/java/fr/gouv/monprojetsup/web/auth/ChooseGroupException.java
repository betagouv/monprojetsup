package fr.gouv.monprojetsup.web.auth;

import fr.gouv.monprojetsup.web.db.DBExceptions;

public class ChooseGroupException extends DBExceptions.UserInputException {

    public ChooseGroupException() { super("Veuillez choisir un groupe."); }

}
