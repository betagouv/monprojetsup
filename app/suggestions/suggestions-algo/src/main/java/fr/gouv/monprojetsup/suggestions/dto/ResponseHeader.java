package fr.gouv.monprojetsup.suggestions.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResponseHeader(

        @Schema(name = "status", description = "status. OK = 0. SERVER_ERROR = 1. USER_ERROR = 2", example = "0", allowableValues = {"0", "1", "2"})
        int status,
        @Schema(name = "error", description = "explication de l'erreur si sttaus != 0.")
        String error,

        @Schema(name = "userMessage", description = "message à afficher à l'utilisateur final.")
        String userMessage
) {
    public ResponseHeader() { this(OK,null, null); }

    public static final int OK = 0;

}