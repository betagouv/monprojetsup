package fr.gouv.monprojetsup.suggestions.server.domain.port

import fr.gouv.monprojetsup.suggestions.algo.NaiveBayesExplanations
import fr.gouv.monprojetsup.suggestions.algo.NaiveBayesScore
import fr.gouv.monprojetsup.suggestions.dto.GetAffinitiesServiceDTO
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO
import fr.gouv.monprojetsup.suggestions.server.commun.exceptions.MPSIllegalStateErrorException
import fr.gouv.monprojetsup.suggestions.server.commun.exceptions.MPSInternalErrorException


data class Suggestions2ServiceRequest(
    val profile: ProfileDTO,
    val keys: List<String>
)

interface Suggestions2Service {
    @Throws(MPSInternalErrorException::class)
    fun recupererLesSuggestions(request: GetAffinitiesServiceDTO.Request): List<NaiveBayesScore>

    @Throws(MPSInternalErrorException::class, MPSIllegalStateErrorException::class)
    fun recupererLesExplications(request: Suggestions2ServiceRequest): NaiveBayesExplanations
}
