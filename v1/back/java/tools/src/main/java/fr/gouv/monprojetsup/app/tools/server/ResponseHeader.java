package fr.gouv.monprojetsup.app.tools.server;

public record ResponseHeader(
        int status,
        String error,
        String userMessage
) {
    public ResponseHeader() { this(OK,null, null); }

    public ResponseHeader(String userMessage) { this(OK,null, userMessage); }

    public ResponseHeader(int status, String error) { this(status,error, null); }
    public static final int OK = 0;
    public static final int SERVER_ERROR = 1;

    public static final int USER_ERROR = 2;

}