package fr.gouv.monprojetsup.tools.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fr.gouv.monprojetsup.web.auth.Authenticator;
import fr.gouv.monprojetsup.web.db.DBExceptions;
import fr.gouv.monprojetsup.web.log.Log;
import fr.gouv.monprojetsup.web.server.WebServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.URI;

import static fr.gouv.monprojetsup.tools.server.Helpers.NULL_DATA;
import static fr.gouv.monprojetsup.tools.server.Helpers.getSanitizedBuffer;

@Service
public abstract class MyService<T,U> implements HttpHandler {

    private final Type classT;

    protected MyService(@NotNull Type classT) {
       this.classT = classT;
    }

    /* we synchronized it to avoid the strange bug */
    @Override
    public synchronized void handle(@NotNull HttpExchange exchange) {
        T req = null;
        try {
            req = extractRequest(exchange);
            if (req == null) throw new RuntimeException(NULL_DATA);
            if(!WebServer.isInitialized()) {
                throw new DBExceptions.ServerStartingException();
            }
            U ans = handleRequest(req);
            Helpers.write(ans, exchange);
        } catch (Exception e) {
            try {
                URI uri = exchange.getRequestURI();
                ErrorResponse response = handleException(e, req, uri == null ? null : uri.toString() );
                Helpers.write(response, exchange);
            }  catch (Exception ignored) {
                //ignored
            }
        }
    }

    protected abstract @NotNull U handleRequest(@NotNull T req) throws Exception;

    private @NotNull T extractRequest(@NotNull HttpExchange t) throws IOException {
        String buffer = getSanitizedBuffer(t);
        try {
            T req = new Gson().fromJson(buffer, classT);
            if (req == null) throw new RuntimeException(NULL_DATA);
            return req;
        } catch (Exception e) {
            throw new RuntimeException("extractRequest failed on buffer " + (buffer == null ? "null" : buffer), e);
        }
    };

    public static ErrorResponse handleException(@Nullable Throwable e, @Nullable Object o, @Nullable String uri) throws IOException {
        Log.logTrace("handleException", e.getMessage());
        if( e == null) {
            return new ErrorResponse(new ResponseHeader(
                    ResponseHeader.SERVER_ERROR,
                    "Null exception"
            ));
        }
        if (e instanceof DBExceptions.UserInputException) {
            String error = e.getMessage();
            if (e instanceof DBExceptions.InvalidTokenException) {
                Helpers.LOGGER.warning(error);
                error = "<p>" + error.replace(System.lineSeparator(), "<br/>") + "</p>";
            }
            if (!(e instanceof DBExceptions.UserInputException.InvalidPasswordException)
                    && !(e instanceof Authenticator.TokenInvalidException)
                    && !(e instanceof DBExceptions.UserInputException.WrongAccessCodeException)
                    && !(e instanceof DBExceptions.UserInputException.UnauthorizedLoginException)
            ) {
                Log.logBackError(error);
            }
            //for those we do not include the stacktrace in the message
            return new ErrorResponse(new ResponseHeader(
                    ResponseHeader.USER_ERROR,
                    e.getMessage()
            ));
        } else {
            //unexpected: we log the error and send back the stacktrace (should be removed in prod)
            try (StringWriter out = new StringWriter()) {
                try {
                    if (uri != null) {
                        out.append(uri).append(System.lineSeparator()).append(System.lineSeparator());
                    } else {
                        out.append("Null URI").append(System.lineSeparator()).append(System.lineSeparator());
                    }
                    //out.append(t.getRemoteAddress().toString());
                    //out.append(t.getLocalAddress().toString());
                } catch (Exception ignores) {
                }
                out.append(e.getMessage() + System.lineSeparator());
                e.printStackTrace(new PrintWriter(out));
                if (o != null) {
                    String buffer = o.toString();
                    int i = buffer.indexOf("password=");
                    if (i >= 0) {
                        int j = buffer.indexOf(",", i);
                        if (j >= 0) {
                            buffer = buffer.substring(0, i) + "password=***" + buffer.substring(j);
                        } else {
                            buffer = buffer.substring(0, i) + "password=***";
                        }
                    }
                    out.append(buffer + System.lineSeparator());
                }

                String error = out.toString();
                Helpers.LOGGER.warning(error);
                error = "<p>" + error.replace(System.lineSeparator(), "<br/>") + "</p>";
                Log.logBackError(error);
                return new ErrorResponse(new ResponseHeader(
                        ResponseHeader.SERVER_ERROR,
                        error
                ));
            }
        }
    }

    public @NotNull U handleRequestAndExceptions(@NotNull T req) throws MyServiceException {
        try {
            return handleRequest(req);
        } catch (Exception e) {
            throw new MyServiceException(e, req);
        }
    }
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
