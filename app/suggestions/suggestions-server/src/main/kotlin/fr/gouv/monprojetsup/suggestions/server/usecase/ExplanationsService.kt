package fr.gouv.monprojetsup.suggestions.server.usecase

import fr.gouv.monprojetsup.suggestions.algo.AlgoSuggestions
import fr.gouv.monprojetsup.suggestions.dto.GetExplanationsAndExamplesServiceDTO
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO
import fr.gouv.monprojetsup.suggestions.server.domain.port.Suggestions2Service
import org.springframework.stereotype.Service


@Service
class ExplanationsService(
    private val algo: AlgoSuggestions,
    private val suggestions2Service: Suggestions2Service,
    ) {
    fun getExplanations(
        profile: ProfileDTO,
        keys: List<String>
    ): List<GetExplanationsAndExamplesServiceDTO.ExplanationAndExamples>? {
        val explnationsNaiveBayes = suggestions2Service.recupererLesExplications(profile, keys)
        return algo.getExplanationsAndExamples(
            profile,
            keys
        )
    }

}