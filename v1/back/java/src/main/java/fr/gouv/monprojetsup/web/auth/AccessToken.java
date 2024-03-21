package fr.gouv.monprojetsup.web.auth;

import fr.gouv.monprojetsup.web.server.WebServer;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.Base64;
import java.util.Random;

public class AccessToken {

    public final @NotNull String token;
    private @NotNull LocalDateTime creation = LocalDateTime.now();
    private @NotNull LocalDateTime lastAccess = LocalDateTime.now();

    public AccessToken(@NotNull String token) {
        this.token = token;
    }

    public void updateLastAccessTime() {
        lastAccess = LocalDateTime.now();
    }
    public void updateCreationTime() {
        creation = LocalDateTime.now();
    }

    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Random random = new Random();

    public static AccessToken getFreshToken() {
        byte[] array = new byte[WebServer.config().getAuthenticationTokenByteLength()]; // length is bounded by 7
        random.nextBytes(array);
        return new AccessToken(encoder.encodeToString(array));
    }

    public Temporal lastAccess() {
        return lastAccess;
    }

    public Temporal creation() {
        return creation;
    }
}

