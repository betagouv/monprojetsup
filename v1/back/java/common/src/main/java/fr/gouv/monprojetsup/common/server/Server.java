package fr.gouv.monprojetsup.common.server;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public abstract class Server {

    public static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    protected abstract void init() throws Exception;


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
