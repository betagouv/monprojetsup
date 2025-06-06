package fr.gouv.monprojetsup.parcoursup.infrastructure.client

import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.commun.client.ApiHttpClient
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.parcoursup.domain.entity.FavorisParcoursup
import fr.gouv.monprojetsup.parcoursup.domain.port.ParcoursupApiFavorisClient
import fr.gouv.monprojetsup.parcoursup.domain.port.ParcoursupAuthentClient
import fr.gouv.monprojetsup.parcoursup.infrastructure.dto.ParcoursupFavorisReponseDTO
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ParcoursupFavorisApiFavorisClient(
    @Value("\${parcoursup.api.favoris.client.id}")
    private val clientId: String,
    @Value("\${parcoursup.api.favoris.client.password}")
    private val clientSecret: String,
    private val parcoursupAuthentClient: ParcoursupAuthentClient,
    @Value("\${parcoursup.api.favoris.url}")
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
    ParcoursupApiFavorisClient {
    @Throws(MonProjetSupInternalErrorException::class)
    override fun recupererLesVoeuxSelectionnesSurParcoursup(idParcoursup: Int): List<FavorisParcoursup> {
        val accessToken = parcoursupAuthentClient.recupererClientAccessToken(clientId = clientId, clientSecret = clientSecret)
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
