package fr.gouv.monprojetsup.parcoursup.infrastructure.client

import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.commun.client.ApiHttpClient
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.parcoursup.domain.entity.FavorisParcoursup
import fr.gouv.monprojetsup.parcoursup.domain.port.ParcoursupHttpClient
import fr.gouv.monprojetsup.parcoursup.infrastructure.dto.ParcoursupFavorisReponseDTO
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ParcoursupApiHttpClient(
    @Value("\${parcoursup.api.client.id}")
    val clientId: String,
    @Value("\${parcoursup.api.client.password}")
    val clientSecret: String,
    @Value("\${parcoursup.api.url.token}")
    val urlToken: String,
    @Value("\${parcoursup.api.url}")
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
    ParcoursupHttpClient {
    @Throws(MonProjetSupInternalErrorException::class)
    override fun recupererLesVoeuxSelectionnesSurParcoursup(idParcoursup: Int): List<FavorisParcoursup> {
        val accessToken = recupererAccessToken(clientId = clientId, clientSecret = clientSecret, urlToken = urlToken)
        val getFavoris =
            get<List<ParcoursupFavorisReponseDTO>>(
                url = baseUrl + URL_FAVORIS + idParcoursup,
                token = accessToken,
            )
        return getFavoris.map { it.toFavorisParcoursup() }
    }

    companion object {
        private const val URL_FAVORIS = "/ApiFavoris/favoris/"
    }
}
