package fr.gouv.monprojetsup.suggestions.server.infrastructure.client

import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.suggestions.dto.GetAffinitiesServiceDTO
import fr.gouv.monprojetsup.suggestions.dto.GetExplanationsAndExamplesServiceDTO
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO
import fr.gouv.monprojetsup.suggestions.dto.suggestions2.Explanations2Answer
import fr.gouv.monprojetsup.suggestions.dto.suggestions2.Suggestions2MultiExplanationsDto
import fr.gouv.monprojetsup.suggestions.dto.suggestions2.Suggestions2AffinityDto
import fr.gouv.monprojetsup.suggestions.dto.suggestions2.Suggestions2Answer
import fr.gouv.monprojetsup.suggestions.server.commun.client.ApiHttpClient
import fr.gouv.monprojetsup.suggestions.server.domain.port.Suggestions2Service
import fr.gouv.monprojetsup.suggestions.server.logging.MpsLogger
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component



@Component
class Suggestion2ApiHttpClient(
    @Value("\${mps.suggestions2.enabled}")
    val enabled: Boolean,
    @Value("\${mps.suggestions2.url}")
    override val baseUrl: String,
    override val objectMapper: ObjectMapper,
    override val httpClient: OkHttpClient,
    override val logger: MpsLogger,
) : ApiHttpClient(baseUrl, objectMapper, httpClient, logger), Suggestions2Service {


    override fun recupererLesSuggestions(profil: ProfileDTO): List<Suggestions2AffinityDto> {
        if(!enabled) {
            logger.info("SUGGESTIONS2", "recupererLesSuggestions: désactivé")
            return emptyList()
        }
        logger.info("SUGGESTIONS2", "recupererLesSuggestions: appel à l'API Suggestions2")
        val reponseDTO =
            post<Suggestions2Answer>(
                url = "$baseUrl/suggestions",
                requeteDTO = GetAffinitiesServiceDTO.Request(profil, true),
            )
        logger.info("SUGGESTIONS2", "recupererLesSuggestions: réponse de l'API Suggestions2")
        return reponseDTO.scores
    }

    override fun recupererLesExplications(request: ProfileDTO, keys: List<String>): List<Suggestions2MultiExplanationsDto> {
        if(!enabled) {
            logger.info("SUGGESTIONS2", "recupererLesExplications: désactivé")
            return emptyList()
        }
        logger.info("SUGGESTIONS2", "recupererLesExplications: appel à l'API Suggestions2")
        val reponseDTO =
            post<Explanations2Answer>(
                url = "$baseUrl/explanations",
                requeteDTO = GetExplanationsAndExamplesServiceDTO.Request(request, keys),
            )
        logger.info("SUGGESTIONS2", "recupererLesExplications: réponse de l'API Suggestions2")
        return reponseDTO.explanations
    }

}
