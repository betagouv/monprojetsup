package fr.gouv.monprojetsup.parcoursup.infrastructure.client

import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.commun.client.ApiHttpClient
import fr.gouv.monprojetsup.commun.client.TokenReponseDTO
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.eleve.domain.entity.ParametresPourRecupererToken
import fr.gouv.monprojetsup.parcoursup.domain.port.IParcoursupAuthentHttpClient
import okhttp3.Credentials
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.stereotype.Component

@Component
class ParcoursupAuthentHttpClient(
    @Value("\${parcoursup.authent.client.id}")
    private val clientId: String,
    @Value("\${parcoursup.authent.client.password}")
    private val clientSecret: String,
    @Value("\${parcoursup.authent.url}")
    override val baseUrl: String,
    override val objectMapper: ObjectMapper,
    override val httpClient: OkHttpClient,
    override val logger: Logger,
) : ApiHttpClient(
        baseUrl,
        objectMapper,
        httpClient,
        logger,
    ),
    IParcoursupAuthentHttpClient {
    @Throws(MonProjetSupInternalErrorException::class)
    override fun recupererClientAccessToken(
        clientId: String,
        clientSecret: String,
    ): String? {
        val urlToken = baseUrl + URL_TOKEN
        val formBody =
            FormBody.Builder()
                .add("grant_type", "client_credentials")
                .build()

        val request =
            Request.Builder()
                .url(urlToken)
                .post(formBody)
                .header("Authorization", Credentials.basic(clientId, clientSecret))
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
                .addQueryParameter("redirect_uri", parametresPourRecupererToken.redirectUri)
                .addQueryParameter("code", parametresPourRecupererToken.code)
                .addQueryParameter("code_verifier", parametresPourRecupererToken.codeVerifier)
                .build()

        val formBody =
            FormBody.Builder()
                .add("grant_type", "client_credentials")
                .build()

        val request =
            Request.Builder()
                .url(url)
                .post(formBody)
                .header("Authorization", Credentials.basic(clientId, clientSecret))
                .build()
        httpClient.newCall(request).execute().use { response ->
            verifierCodeErreur(response, url.toString())
            val bodyRetour = deserialisation<TokenReponseDTO>(response.body?.string(), url.toString())
            val idToken = NimbusJwtDecoder.withJwkSetUri("$baseUrl/oauth2/jwk").build().decode(bodyRetour.idToken)
            return idToken.getClaim<String>("id").toInt()
        }
    }

    companion object {
        private const val URL_TOKEN = "/oauth2/token"
    }
}
