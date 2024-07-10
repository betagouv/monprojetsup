package fr.gouv.monprojetsup.app.server;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResponseHeader(

        @Schema(name = "status", description = "status. OK = 0. SERVER_ERROR = 1. USER_ERROR = 2", example = "0", allowableValues = {"0", "1", "2"})
        int status,
        @Schema(name = "error", description = "explication de l'erreur si sttaus != 0.", example = "", required = false)
        String error,

        @Schema(name = "userMessage", description = "message à afficher à l'utilisateur final.", example = "", required = false)
        String userMessage
) {
    public ResponseHeader() { this(OK,null, null); }

    public ResponseHeader(String userMessage) { this(OK,null, userMessage); }

    public ResponseHeader(int status, String error) { this(status,error, null); }
    public static final int OK = 0;
    public static final int SERVER_ERROR = 1;

    public static final int USER_ERROR = 2;

}