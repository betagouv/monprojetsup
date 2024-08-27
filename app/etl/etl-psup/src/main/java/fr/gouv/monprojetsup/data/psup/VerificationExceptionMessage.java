package fr.gouv.monprojetsup.data.psup;

public enum VerificationExceptionMessage {

    MESSAGE("%s");

    private final String message;

    VerificationExceptionMessage(@SuppressWarnings("SameParameterValue") String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}