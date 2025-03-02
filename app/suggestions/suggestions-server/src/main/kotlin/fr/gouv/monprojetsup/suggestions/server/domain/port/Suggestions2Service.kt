package fr.gouv.monprojetsup.suggestions.server.domain.port

import fr.gouv.monprojetsup.suggestions.dto.GetAffinitiesServiceDTO.Affinity
import fr.gouv.monprojetsup.suggestions.dto.GetExplanationsAndExamplesServiceDTO.ExplanationAndExamples
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO
import fr.gouv.monprojetsup.suggestions.server.commun.exceptions.MPSIllegalStateErrorException
import fr.gouv.monprojetsup.suggestions.server.commun.exceptions.MPSInternalErrorException

interface Suggestions2Service {
    @Throws(MPSInternalErrorException::class)
    fun recupererLesSuggestions(profil: ProfileDTO): List<Affinity>

    @Throws(MPSInternalErrorException::class, MPSIllegalStateErrorException::class)
    fun recupererLesExplications(
        request: ProfileDTO,
        keys: List<String>
    ): List<ExplanationAndExamples>
}
