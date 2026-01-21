package fr.gouv.monprojetsup.commun.auth.infrastructure

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.commun.client.ApiHttpClient
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import okhttp3.OkHttpClient
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class TokenReponseDTO(
    @JsonProperty(value = "access_token")
    val token: String,
    @JsonProperty(value = "token_type")
    val tokenType: String?,
    @JsonProperty(value = "expires_in")
    val expiresIn: String?,
)

data class BearerToken(
    val token: String,
    val expiryDateTime: LocalDateTime,
) {
    constructor(token: String, expiresIn: Long) :
        this(token = token, expiryDateTime = LocalDateTime.now().plusSeconds(expiresIn - 1))

    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiryDateTime)
    }
}

class BearerTokenProviderService(
    val clientId: String,
    val clientSecret: String,
    val authUri: String,
    override val httpClient: OkHttpClient,
    override val objectMapper: ObjectMapper,
    override val logger: MonProjetSupLogger,
) : ApiHttpClient(authUri, objectMapper, httpClient, logger) {
    private var currentToken: BearerToken? = null

    companion object {
        private const val ERREUR_AUTH_CLIENT_AVENIRS = "ERREUR_AUTH_CLIENT_AVENIRS"
    }

    /**
     * Récupère un token d'accès valide.
     *
     * @return String contenant le token.
     */
    @Synchronized
    fun getToken(): String {
        var bearerToken = currentToken
        if (bearerToken == null || bearerToken.isExpired()) {
            bearerToken = fetchNewToken()
            currentToken = bearerToken
        }
        return bearerToken.token
    }

    private fun fetchNewToken(): BearerToken {
        val responseDTO =
            post<TokenReponseDTO>(
                url = authUri,
                requeteDTO =
                    mapOf(
                        "grant_type" to "client_credentials",
                        "client_id" to clientId,
                        "client_secret" to clientSecret,
                    ),
            )
        if (responseDTO.tokenType != "bearer") {
            throw MonProjetSupInternalErrorException(
                ERREUR_AUTH_CLIENT_AVENIRS,
                "Le type du token d'authentification n'est pas 'bearer', mais '$responseDTO.tokenType'",
            )
        }
        val expiresIn =
            responseDTO.expiresIn
                ?: throw MonProjetSupInternalErrorException(
                    ERREUR_AUTH_CLIENT_AVENIRS,
                    "La durée de vie du token n'a pas été trouvée dans la réponse",
                )
        val bearerToken = BearerToken(responseDTO.token, expiresIn.toLong())
        if (bearerToken.isExpired()) {
            throw MonProjetSupInternalErrorException(
                ERREUR_AUTH_CLIENT_AVENIRS,
                "Le token d'authentification est déjà expiré",
            )
        }
        return bearerToken
    }
}
