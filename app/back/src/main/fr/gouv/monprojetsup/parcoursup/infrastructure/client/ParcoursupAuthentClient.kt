package fr.gouv.monprojetsup.parcoursup.infrastructure.client

import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.commun.client.ApiHttpClient
import fr.gouv.monprojetsup.commun.client.TokenReponseDTO
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.eleve.domain.entity.ParametresPourRecupererToken
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.parcoursup.domain.port.ParcoursupAuthentClient
import okhttp3.Credentials
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ParcoursupAuthentClient(
    private val jwtDecoder: ParcoursupJwtDecoder,
    @Value("\${parcoursup.authent.client.id}")
    private val clientId: String,
    @Value("\${parcoursup.authent.client.password}")
    private val clientSecret: String,
    @Value("\${parcoursup.authent.url}")
    override val baseUrl: String,
    override val objectMapper: ObjectMapper,
    override val httpClient: OkHttpClient,
    override val logger: MonProjetSupLogger,
) : ApiHttpClient(
        baseUrl,
        objectMapper,
        httpClient,
        logger,
    ),
    ParcoursupAuthentClient {
    @Throws(MonProjetSupInternalErrorException::class)
    override fun recupererClientAccessToken(
        clientId: String,
        clientSecret: String,
    ): String? {
        val urlToken = baseUrl + URL_TOKEN
        val formBody =
            FormBody.Builder()
                .add(GRANT_TYPE, GRANT_TYPE_CLIENT_CREDENTIALS)
                .build()

        val request =
            Request.Builder()
                .url(urlToken)
                .post(formBody)
                .header(HEADER_AUTHORIZATION, Credentials.basic(clientId, clientSecret))
                .build()

        httpClient.newCall(request).execute().use { response ->
            verifierCodeErreur(response, urlToken)
            val bodyRetour = deserialisation<TokenReponseDTO>(response.body?.string(), urlToken)
            return bodyRetour.token
        }
    }

    @Throws(MonProjetSupBadRequestException::class)
    override fun recupererIdParcoursupEleve(parametresPourRecupererToken: ParametresPourRecupererToken): Int {
        val url =
            (baseUrl + URL_TOKEN).toHttpUrl().newBuilder()
                .addQueryParameter(GRANT_TYPE, GRANT_TYPE_AUTHORIZATION_CODE)
                .addQueryParameter(REDIRECT_URI, parametresPourRecupererToken.redirectUri)
                .addQueryParameter(CODE, parametresPourRecupererToken.code)
                .addQueryParameter(CODE_VERIFIER, parametresPourRecupererToken.codeVerifier)
                .build()

        val formBody = FormBody.Builder().build()
        val request =
            Request.Builder()
                .url(url)
                .header(HEADER_AUTHORIZATION, Credentials.basic(clientId, clientSecret))
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_URL_ENCODED)
                .post(formBody)
                .build()

        httpClient.newCall(request).execute().use { response ->
            verifierCodeErreur(response, url.toString())
            val bodyRetour = deserialisation<TokenReponseDTO>(response.body?.string(), url.toString())
            try {
                val idToken = jwtDecoder.decode(bodyRetour.token)
                return idToken.getClaim<String>(CLAIM_ID).toInt()
            } catch (exception: Exception) {
                throw MonProjetSupInternalErrorException(
                    code = "ACCESS_TOKEN_INDECHIFFRABLE",
                    msg = "Le JWT renvoyé n'a pas su être désérialisé",
                    origine = exception,
                )
            }
        }
    }

    companion object {
        private const val URL_TOKEN = "/oauth2/token"
        private const val CLAIM_ID = "sub"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_CONTENT_TYPE = "Content-Type"
        private const val CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded"
        private const val GRANT_TYPE = "grant_type"
        private const val GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code"
        private const val GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials"
        private const val REDIRECT_URI = "redirect_uri"
        private const val CODE = "code"
        private const val CODE_VERIFIER = "code_verifier"
    }
}
