package fr.gouv.monprojetsup.common.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;

@Slf4j
@Service
public abstract class MyService<T,U> implements HttpHandler {

    private final Type classT;

    protected MyService(@NotNull Type classT) {
        this.classT = classT;
    }

    protected abstract boolean isServerReady();

    /* we synchronized it to avoid the strange bug */
    @Override
    public synchronized void handle(@NotNull HttpExchange exchange) {
        T req = null;
        try {
            req = extractRequest(exchange);
            if(!isServerReady()) {
                throw new ServerStartingException();
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

    public abstract ErrorResponse handleException(@Nullable Throwable e, @Nullable Object o, @Nullable String uri) throws Exception;

    public @NotNull U handleRequestAndExceptions(@NotNull T req) throws MyServiceException {
        try {
            return handleRequest(req);
        } catch (Exception e) {
            throw new MyServiceException(e, req);
        }
    }


}
