package fr.gouv.monprojetsup.suggestions.server.usecase

import fr.gouv.monprojetsup.suggestions.algo.AlgoSuggestions
import fr.gouv.monprojetsup.suggestions.algo.DataSuggestions2
import fr.gouv.monprojetsup.suggestions.dto.GetAffinitiesServiceDTO
import fr.gouv.monprojetsup.suggestions.server.domain.port.Suggestions2Service
import org.springframework.stereotype.Service


@Service
class SuggestionsService(
    private val algo: AlgoSuggestions,
    private val suggestions2Service: Suggestions2Service,
    ) {
    fun getSuggestions(request: GetAffinitiesServiceDTO.Request): GetAffinitiesServiceDTO.Response {
        val withDetails = request.inclureExplicationsDetaillees ?: false
        val naiveBayesAffinities = suggestions2Service.recupererLesSuggestions(request)
        val suggestions = algo.getFormationsSuggestions(
            request.profile,
            request.inclureScores?: true,
            naiveBayesAffinities.stream().map { DataSuggestions2(it.key, it.scores, null) }.toList(),
            withDetails
        )
        val metiers = algo.sortMetiersByAffinites(
            request.profile,
            null,
            withDetails
        )
        return GetAffinitiesServiceDTO.Response(
            suggestions,
            metiers
        )
    }

}