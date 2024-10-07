package fr.gouv.monprojetsup.parcoursup.infrastructure.client

import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.commun.client.ApiHttpClient
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.parcoursup.domain.entity.FavorisParcoursup
import fr.gouv.monprojetsup.parcoursup.domain.port.ParcoursupHttpClient
import fr.gouv.monprojetsup.parcoursup.infrastructure.dto.ParcoursupFavorisReponseDTO
import okhttp3.OkHttpClient
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ParcoursupApiHttpClient(
    @Value("\${parcoursup.api.client.id}")
    private val clientId: String,
    @Value("\${parcoursup.api.client.password}")
    private val clientSecret: String,
    @Value("\${parcoursup.api.url.authent}")
    private val urlAuthent: String,
    @Value("\${parcoursup.api.url}")
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
    ParcoursupHttpClient {
    @Throws(MonProjetSupInternalErrorException::class)
    override fun recupererLesVoeuxSelectionnesSurParcoursup(idParcoursup: Int): List<FavorisParcoursup> {
        val accessToken = recupererAccessToken(clientId = clientId, clientSecret = clientSecret, urlToken = urlAuthent + URL_TOKEN)
        val getFavoris =
            get<List<ParcoursupFavorisReponseDTO>>(
                url = baseUrl + URL_FAVORIS + idParcoursup,
                token = accessToken,
            )
        return getFavoris.map { it.toFavorisParcoursup() }
    }

    @Throws(MonProjetSupBadRequestException::class)
    override fun recupererIdParcoursupEleve(jwt: String): Int {
        val accessToken = recupererAccessToken(clientId = clientId, clientSecret = clientSecret, urlToken = urlAuthent + URL_TOKEN)
        val getUserInfo =
            post<String>(
                urlAuthent + URL_USER_INFO,
                "token=$jwt",
                accessToken,
            )
        return 0
    }

    companion object {
        private const val URL_TOKEN = "/oauth2/token"
        private const val URL_USER_INFO = "/userinfo"
        private const val URL_FAVORIS = "/ApiFavoris/favoris/"
    }
}
