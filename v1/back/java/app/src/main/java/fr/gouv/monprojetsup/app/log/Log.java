package fr.gouv.monprojetsup.app.log;

import com.google.gson.Gson;
import fr.gouv.monprojetsup.app.auth.Authenticator;
import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.mail.MailSender;
import fr.gouv.monprojetsup.app.server.WebServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Log {

    public static Map<String, LocalDateTime> lastEmailSent = new ConcurrentHashMap<>();

    public static boolean checkLastEmailWasSentAReasonableLongTimeAgo(String key) {
        if(key == null) key = "null";
        LocalDateTime lastEmail = lastEmailSent.get(key);
        boolean answer = lastEmail == null || lastEmail.plusSeconds(1).isBefore(LocalDateTime.now());
        if(answer) lastEmailSent.put(key, LocalDateTime.now());
        return answer;
    }

    public static void logFrontError(String error) {
        WebServer.LOGGER.warning(error);
        if(checkLastEmailWasSentAReasonableLongTimeAgo(error)) {
            WebServer.db().logFrontError(error);
            MailSender.send(
                    WebServer.config().getEmailConfig(),
                    WebServer.config().getErrorsEmailAddress(),
                    "MonProjetSup: front error",
                    error
            );
        }
    }

    public static void logBackError(String error) {
        WebServer.LOGGER.warning(error);
         if(checkLastEmailWasSentAReasonableLongTimeAgo(error)) {
             DB db = WebServer.db();
             if(db != null) {
                 db.logBackError(error);
             }
             MailSender.send(
                     WebServer.config().getEmailConfig(),
                     WebServer.config().getErrorsEmailAddress(),
                     "MonProjetSup: back error",
                     error
             );
         }
    }

    public static void logTrace(@NotNull String origin, @NotNull String trace) {
        WebServer.LOGGER.info(origin + " - " + trace);
        @Nullable DB db = WebServer.db();
        if(db != null) {
            db.logTrace(origin, trace);
        }
    }

    public static void logTrace(@NotNull String origin, @NotNull String trace, @NotNull Object object) {
        WebServer.LOGGER.info(origin + " - " + trace + " - " + new Gson().toJson(object));
        @Nullable DB db = WebServer.db();
        if(db != null) {
            db.logTrace(origin, trace, object);
        }
    }

    public static void logBackError(Throwable e) {
        if (!(e instanceof DBExceptions.UserInputException.InvalidPasswordException)
                && !(e instanceof Authenticator.TokenInvalidException)
                && !(e instanceof DBExceptions.UserInputException.WrongAccessCodeException)
                && !(e instanceof DBExceptions.UserInputException.UnauthorizedLoginException)
        ) {
            String error = e.getMessage();
            if (e instanceof DBExceptions.InvalidTokenException) {
                WebServer.LOGGER.warning(error);
                error = "<p>" + error.replace(System.lineSeparator(), "<br/>") + "</p>";
            }
            logBackError(error);
        }

    }
}
