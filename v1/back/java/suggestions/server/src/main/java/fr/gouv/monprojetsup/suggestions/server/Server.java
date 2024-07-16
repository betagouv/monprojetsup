package fr.gouv.monprojetsup.suggestions.server;

import fr.gouv.monprojetsup.suggestions.dto.ResponseHeader;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public abstract class Server {

    public static final Logger LOGGER = Logger.getLogger(Server.class.getName());


    public record BasicRequest(
            @NotNull String login,
            @NotNull String token
    ) {
    }

    public record EmptyRequest(
    ) {
    }

    public  record BasicResponse(
            ResponseHeader header
    ) {
        public BasicResponse() { this(new ResponseHeader()); }
    }
}
