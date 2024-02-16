package fr.gouv.monprojetsup.web.auth;

import org.springframework.security.web.csrf.CsrfToken;

import java.time.LocalDateTime;

public class CustomCsrfToken implements CsrfToken {

    private AccessToken token;
    private String headerName;
    private String parameterName;

    public CustomCsrfToken(AccessToken token, String headerName, String parameterName) {
        this.token = token;
        this.headerName = headerName;
        this.parameterName = parameterName;
    }

    @Override
    public String getHeaderName() {
        return headerName;
    }

    @Override
    public String getParameterName() {
        return parameterName;
    }

    @Override
    public String getToken() {
        return token.token;
    }
}
