package fr.gouv.monprojetsup.suggestions.server.usecase

import fr.gouv.monprojetsup.suggestions.algo.AlgoSuggestions
import fr.gouv.monprojetsup.suggestions.dto.GetAffinitiesServiceDTO
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO
import fr.gouv.monprojetsup.suggestions.server.domain.port.Suggestions2Service
import org.springframework.stereotype.Service


@Service
class SuggestionsService(
    private val algo: AlgoSuggestions,
    private val suggestions2Service: Suggestions2Service,
    ) {
    fun getSuggestions(profil: ProfileDTO, inclureScores: Boolean?): GetAffinitiesServiceDTO.Response {
        val naiveBayesAffinities = suggestions2Service.recupererLesSuggestions(profil)
        val suggestions = algo.getFormationsSuggestions(
            profil,
            inclureScores?: true,
            naiveBayesAffinities
        )
        val metiers = algo.sortMetiersByAffinites(
            profil,
            null
        )
        return GetAffinitiesServiceDTO.Response(
            suggestions,
            metiers
        )
    }

}