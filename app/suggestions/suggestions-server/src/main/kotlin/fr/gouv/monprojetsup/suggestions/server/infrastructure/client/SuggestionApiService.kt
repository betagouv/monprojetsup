package fr.gouv.monprojetsup.formation.infrastructure.client

import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.logging.MpsLogger
import fr.gouv.monprojetsup.suggestions.dto.GetAffinitiesServiceDTO
import fr.gouv.monprojetsup.suggestions.dto.GetExplanationsAndExamplesServiceDTO
import fr.gouv.monprojetsup.suggestions.dto.GetExplanationsAndExamplesServiceDTO.ExplanationAndExamples
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO
import fr.gouv.monprojetsup.suggestions.server.commun.client.ApiHttpClient
import fr.gouv.monprojetsup.suggestions.server.domain.port.Suggestions2Service
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SuggestionApiService(
    @Value("\${mps.suggestions2.enabled}")
    val enabled: Boolean,
    @Value("\${mps.suggestions2.url}")
    override val baseUrl: String,
    override val objectMapper: ObjectMapper,
    override val httpClient: OkHttpClient,
    override val logger: MpsLogger,
) : ApiHttpClient(baseUrl, objectMapper, httpClient, logger), Suggestions2Service {

    override fun recupererLesSuggestions(profil: ProfileDTO): List<GetAffinitiesServiceDTO.Affinity> {
        if(!enabled) {
            return emptyList()
        }
        val reponseDTO =
            post<GetAffinitiesServiceDTO.Response>(
                url = "$baseUrl/suggestions",
                requeteDTO = GetAffinitiesServiceDTO.Request(profil, true),
            )
        return reponseDTO.affinites
    }

    override fun recupererLesExplications(request: ProfileDTO, keys: List<String>): List<ExplanationAndExamples> {
        if(!enabled) {
            return emptyList()
        }
        val reponseDTO =
            post<GetExplanationsAndExamplesServiceDTO.Response>(
                url = "$baseUrl/suggestions",
                requeteDTO = request,
            )
        return reponseDTO.liste
    }

}
