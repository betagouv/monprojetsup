package fr.gouv.monprojetsup.suggestions.server.domain.port

import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO
import fr.gouv.monprojetsup.suggestions.dto.suggestions2.Suggestions2AffinityDto
import fr.gouv.monprojetsup.suggestions.dto.suggestions2.Suggestions2MultiExplanationsDto
import fr.gouv.monprojetsup.suggestions.server.commun.exceptions.MPSIllegalStateErrorException
import fr.gouv.monprojetsup.suggestions.server.commun.exceptions.MPSInternalErrorException

interface Suggestions2Service {
    @Throws(MPSInternalErrorException::class)
    fun recupererLesSuggestions(profil: ProfileDTO): List<Suggestions2AffinityDto>

    @Throws(MPSInternalErrorException::class, MPSIllegalStateErrorException::class)
    fun recupererLesExplications(
        request: ProfileDTO,
        keys: List<String>
    ): List<Suggestions2MultiExplanationsDto>
}
