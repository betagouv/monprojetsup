package fr.gouv.monprojetsup.app.server;

import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.log.Log;
import fr.gouv.monprojetsup.common.server.ErrorResponse;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Objects;

@Slf4j
@Service
public abstract class MyService<T,U> extends fr.gouv.monprojetsup.common.server.MyService<T,U> {


    protected MyService(@NotNull Type classT) {
        super(classT);
    }

    @Override
    public ErrorResponse handleException(@Nullable Throwable e, @Nullable Object o, @Nullable String uri) throws IOException {
        return handleAnException(e, o, uri);
    }

    public static ErrorResponse handleAnException(@Nullable Throwable e, @Nullable Object o, @Nullable String uri) throws IOException {
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

    @Override
    protected boolean isServerReady() {
        return WebServer.isInitialized();
    }

}
