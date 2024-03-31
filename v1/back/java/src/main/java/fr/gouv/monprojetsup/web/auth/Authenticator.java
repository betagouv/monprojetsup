package fr.gouv.monprojetsup.web.auth;

import fr.gouv.monprojetsup.web.db.DBExceptions;
import fr.gouv.monprojetsup.web.db.model.User;
import fr.gouv.monprojetsup.web.server.WebServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static fr.gouv.monprojetsup.web.db.model.User.normalizeUser;


@Service
public class Authenticator {




    public static class SessionExpiredException extends Exception {
        public SessionExpiredException() {
            super("Session expirée, veuillez vous reconnecter.");
        }
    }

    public static class TokenInvalidException extends DBExceptions.UserInputException {
        public TokenInvalidException() {
            super("Le serveur a été redémarré pendant votre navigation, veuillez vous reconnecter (jeton de session incorrect).");
        }
    }


    //maps login to token
    private final Map<String, AccessToken> tokens = new ConcurrentHashMap<>();

    private static void internalAuthenticate(String password, String salt, String hash) throws NoSuchAlgorithmException, InvalidKeySpecException, DBExceptions.UserInputException.InvalidPasswordException {
        if(password == null) throw new RuntimeException("Null data");
        String inputHash = Credential.computeHash(password,salt);
        if(!WebServer.config().isNoAuthentificationMode() && !inputHash.equals(hash)) {
            throw new DBExceptions.UserInputException.InvalidPasswordException();
        }
    }

    public static boolean matches(@Nullable CharSequence rawPassword, @Nullable String encodedPassword) {
        //TODO: protect against timing attacks
        try {
            if (encodedPassword == null || rawPassword == null) return false;
            int i = encodedPassword.indexOf("$--$");
            if (i < 0) return false;
            String hash = encodedPassword.substring(0, i);
            String salt = encodedPassword.substring(i + 4);
            internalAuthenticate(rawPassword.toString(), salt, hash);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private void checkSessionExpiration(AccessToken t) throws SessionExpiredException {
        if(t == null) {
            throw new SessionExpiredException();
        }
        Duration d = Duration.between(LocalDateTime.now(), t.lastAccess());
        Duration d2 = Duration.between(LocalDateTime.now(), t.creation());
        if(d.getSeconds() / 60 > WebServer.config().getAuthenticationTokenLifeSpanWithoutActionMinutes()
                || d2.getSeconds() / 60 > WebServer.config().getAuthenticationTokenLifeSpanSinceCreationMinutes() ) {
            throw new SessionExpiredException();
        }
    }

    public void tokenAuthenticate(String login, String token) throws TokenInvalidException, SessionExpiredException {
        if(login == null || token == null) throw new RuntimeException("Null data");
        login = normalizeUser(login);
        //concurrent hashmap
        AccessToken t = tokens.get(login);
        if(t == null) {
            throw new TokenInvalidException();
        }
        checkSessionExpiration(t);
        if(!t.token.equals(token)) {
            throw new TokenInvalidException();
        }
        //updates the date of last token access
        t.updateLastAccessTime();
    }

    public String authenticateAndGetToken(@Nullable User user, @Nullable String inputPassword) throws NoSuchAlgorithmException, InvalidKeySpecException, DBExceptions.UserInputException.InvalidPasswordException, DBExceptions.UserInputException.ForbiddenException {
        if(user == null || inputPassword == null) throw new DBExceptions.UserInputException.ForbiddenException();
        internalAuthenticate(inputPassword, user.cr().salt(), user.cr().hash());

        //we computeifabsent to allow simultaneous login from two different machines
        AccessToken token = tokens.computeIfAbsent(user.login(), z -> AccessToken.getFreshToken());

        //we update creation time: every successful authentication resets the clock
        token.updateCreationTime();
        token.updateLastAccessTime();
        //int i = 0 / 0;
        return token.token;
    }

    public @NotNull String getTokenForUserAuthenticatedViaOidbc(@NotNull String login) {
        AccessToken token = tokens.computeIfAbsent(login, z -> AccessToken.getFreshToken());
        token.updateCreationTime();
        token.updateLastAccessTime();
        return token.token;
    }
    /*

    @NotNull
    public static CsrfToken generateToken(String headerName, String parameterName) {
        return new CustomCsrfToken(
                AccessToken.getFreshToken(),
                headerName,
                parameterName
        );
    }
*/


    public void logout(String login, String token) throws TokenInvalidException, SessionExpiredException {
        login = normalizeUser(login);
        tokenAuthenticate(login, token);
        forget(login);
    }


    public void forget(String login) {
        tokens.keySet().remove(login);
    }

    /**
     * checks super user right
     * @param login
     * @param token
     * @return
     * @throws TokenInvalidException
     */
    public boolean checkIsSuperUser(String login, String token) throws TokenInvalidException, SessionExpiredException {
        tokenAuthenticate(login, token);
        return WebServer.config().getAdmins().contains(login);
    }


}
