package fr.gouv.monprojetsup.suggestions.server.domain.port

import fr.gouv.monprojetsup.suggestions.dto.GetAffinitiesServiceDTO
import fr.gouv.monprojetsup.suggestions.dto.GetExplanationsAndExamplesServiceDTO
import fr.gouv.monprojetsup.suggestions.dto.suggestions2.NaiveBayesExplanations
import fr.gouv.monprojetsup.suggestions.dto.suggestions2.Suggestions2SuggestionsDto
import fr.gouv.monprojetsup.suggestions.server.commun.exceptions.MPSIllegalStateErrorException
import fr.gouv.monprojetsup.suggestions.server.commun.exceptions.MPSInternalErrorException


interface Suggestions2Service {
    @Throws(MPSInternalErrorException::class)
    fun recupererLesSuggestions(request: GetAffinitiesServiceDTO.Request): List<Suggestions2SuggestionsDto>

    @Throws(MPSInternalErrorException::class, MPSIllegalStateErrorException::class)
    fun recupererLesExplications(request: GetExplanationsAndExamplesServiceDTO): NaiveBayesExplanations
}
