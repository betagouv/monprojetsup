package fr.gouv.monprojetsup.web.services.accounts;

import fr.gouv.monprojetsup.tools.server.MyService;
import fr.gouv.monprojetsup.web.db.DBExceptions;
import fr.gouv.monprojetsup.web.log.Log;
import fr.gouv.monprojetsup.web.server.WebServer;
import fr.gouv.monprojetsup.web.server.WebServerConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class OidcLoginService extends MyService<OidcLoginService.JwtEncoded, PasswordLoginService.Response> {

    public OidcLoginService() {
        super(String.class);
    }

    public record JwtDecoded(
            String email,
            Boolean email_verified,

            String iss,

            String oauthProvider
    ) {
    }

    public record JwtEncoded(
            String jwt
    ) {
    }
    @Override
    protected @NotNull PasswordLoginService.Response handleRequest(@NotNull OidcLoginService.JwtEncoded jwtEncoded) throws Exception {

        if(WebServer.db() == null) {
            throw new DBExceptions.ServerStartingException();
        }

        Key key = getKey(jwtEncoded);

        /* the parseClaimsJws method throws
            UnsupportedJwtException – if the claimsJws argument does not represent a Claims JWS
            MalformedJwtException – if the claimsJws string is not a valid JWS
            SignatureException – if the claimsJws JWS signature validation fails
            ExpiredJwtException – if the specified JWT is a Claims JWT and the Claims has an expiration time before the time this method is invoked.
         */
        val claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwtEncoded.jwt)
                .getBody();

        //unclear whether the following is redundant with the previous call to parseClaimsJws
        if(isJwtExpired(claims)) throw new DBExceptions.UserInputException.JwtExpired();
        if(!isIssuerGoogle(claims)) throw new DBExceptions.UserInputException.JwtWrongIssuer();

        @Nullable Object email = claims.get("email");
        @Nullable Object email_verified = claims.get("email_verified");
        if(!(email instanceof String) || !(email_verified instanceof Boolean)) {
            throw new DBExceptions.UnknownUserException("Email non précisé");
        }
        //here both email and email_verified are non-null

        if(Boolean.FALSE.equals(email_verified)) throw new DBExceptions.UnknownUserException("Email non vérifié");

        Log.logTrace((String) email, " logging in with certified JWT from " + claims.getIssuer() + " with email " + email);
        PasswordLoginService.LoginAnswer answer
                = WebServer.db().authenticateWithOidc((String) email);
        if (answer.token() != null) Log.logTrace(answer.login(),  " logged in with jwt");
        return new PasswordLoginService.Response(answer);
    }


    /**
     * @param claims
     * @return true if the token is issued by Google
     */
    private boolean isIssuerGoogle(Claims claims) {
       return WebServerConfig.GOOGLE_ISS.equals(claims.getIssuer());
    }

    /**
     * @param claims
     * @return true if the token is expired
     */
    private boolean isJwtExpired(Claims claims) {
        return claims.getExpiration().getTime() < System.currentTimeMillis();
    }

    private static final HttpsJwksVerificationKeyResolver httpsJwksKeyResolver
            = new HttpsJwksVerificationKeyResolver(new HttpsJwks(WebServerConfig.GOOGLE_API_CERTS_URI));

    /**
     * @param jwtEncoded the jwt to decode
     * @return the key used to sign the token
     * @throws JoseException
     */
    private Key getKey(JwtEncoded jwtEncoded) throws JoseException {

        val jws = new JsonWebSignature();
        jws.setCompactSerialization(jwtEncoded.jwt);
        Key key = httpsJwksKeyResolver.resolveKey(jws, null);
        return key;
    }

}
