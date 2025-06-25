package fr.gouv.monprojetsup.suggestions.server.infrastructure.client

import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.suggestions.dto.GetAffinitiesServiceDTO
import fr.gouv.monprojetsup.suggestions.dto.ResponseHeader
import fr.gouv.monprojetsup.suggestions.entities.NaiveBayesExplanations
import fr.gouv.monprojetsup.suggestions.entities.NaiveBayesSuggestions
import fr.gouv.monprojetsup.suggestions.entities.Suggestions2ExplanationsDto
import fr.gouv.monprojetsup.suggestions.server.commun.client.ApiHttpClient
import fr.gouv.monprojetsup.suggestions.server.domain.port.Suggestions2Service
import fr.gouv.monprojetsup.suggestions.server.domain.port.Suggestions2ServiceRequest
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

    data class Suggestions2Answer(
        val header: ResponseHeader = ResponseHeader(),
        val scores: List<NaiveBayesSuggestions> = emptyList()
    )

    override fun recupererLesSuggestions(request: GetAffinitiesServiceDTO.Request): List<NaiveBayesSuggestions> {
        if(!enabled) {
            logger.info("SUGGESTIONS2", "recupererLesSuggestions: désactivé")
            return emptyList()
        }
        val suggestionsDto =
            post<Suggestions2Answer>(
                url = "$baseUrl/suggestions",
                requeteDTO = request
            )
        if(suggestionsDto.header.status == 0) {
            logger.info("SUGGESTIONS2", "recupererLesSuggestions: appel réussi à l'API Suggestions2")
            return suggestionsDto.scores
        } else {
            logger.error("SUGGESTIONS2", "recupererLesSuggestions: erreur lors de l'appel à l'API Suggestions2, status: ${suggestionsDto.header.status}}")
            throw RuntimeException("Erreur lors de l'appel à l'API Suggestions2: error ${suggestionsDto.header.error} msg ${suggestionsDto.header.userMessage}")
        }
    }

    data class Explanations2Answer(
        val header : ResponseHeader =  ResponseHeader(),
        val explanations: List<Suggestions2ExplanationsDto> = emptyList(),
        val scores: List<NaiveBayesSuggestions> = emptyList()
    )

    override fun recupererLesExplications(request: Suggestions2ServiceRequest): NaiveBayesExplanations {
        if(!enabled) {
            logger.info("SUGGESTIONS2", "recupererLesExplications: désactivé")
            return NaiveBayesExplanations()
        }
        val suggestionsDto =
            post<Suggestions2Answer>(
                url = "$baseUrl/suggestions",
                requeteDTO = request,
            )
        val explanationDto =
            post<Explanations2Answer>(
                url = "$baseUrl/explanations",
                requeteDTO = request
            )
        if(explanationDto.header.status == 0) {
            logger.info("SUGGESTIONS2", "recupererLesExplications: appel réussi à l'API Suggestions2")
            return NaiveBayesExplanations(
                suggestionsDto.scores,
                explanationDto.explanations
            )
        } else {
            logger.error("SUGGESTIONS2", "recupererLesExplications: erreur lors de l'appel à l'API Suggestions2, status: ${explanationDto.header.status}")
            throw RuntimeException("Erreur lors de l'appel à l'API Suggestions2: error ${explanationDto.header.error} msg ${explanationDto.header.userMessage}")
        }

    }

}
