package fr.gouv.monprojetsup.app.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.log.Log;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.tools.server.ErrorResponse;
import fr.gouv.monprojetsup.app.tools.server.Helpers;
import fr.gouv.monprojetsup.app.tools.server.MyServiceException;
import fr.gouv.monprojetsup.app.tools.server.ResponseHeader;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Objects;

@Slf4j
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
        String buffer = Helpers.getSanitizedBuffer(t);
        try {
            T req = new Gson().fromJson(buffer, classT);
            if (req == null) throw new RuntimeException(Helpers.NULL_DATA);
            return req;
        } catch (Exception e) {
            throw new RuntimeException("extractRequest failed on buffer " + (buffer == null ? "null" : buffer), e);
        }
    }

    public static ErrorResponse handleException(@Nullable Throwable e, @Nullable Object o, @Nullable String uri) throws IOException {
        if( e == null) {
            return new ErrorResponse(new ResponseHeader(
                    ResponseHeader.SERVER_ERROR,
                    "Null exception"
            ));
        }
        Log.logTrace("handleException", e.getMessage());
        if (e instanceof DBExceptions.UserInputException) {
            Log.logBackError(e);
            //for those we do not include the stacktrace in the message
            return new ErrorResponse(new ResponseHeader(
                    ResponseHeader.USER_ERROR,
                    e.getMessage()
            ));
        } else {
            //unexpected: we log the error and send back the stacktrace (should be removed in prod)
            try (StringWriter out = new StringWriter()) {
                try {
                    out.append(Objects.requireNonNullElse(uri, "Null URI")).append(System.lineSeparator()).append(System.lineSeparator());
                } catch (Exception ignores) {
                }
                out.append(e.getMessage()).append(System.lineSeparator());
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
                    out.append(buffer).append(System.lineSeparator());
                }

                String error = out.toString();
                log.warn(error);
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
