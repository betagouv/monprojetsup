package fr.gouv.monprojetsup.suggestions.server.usecase

import fr.gouv.monprojetsup.suggestions.algo.AlgoSuggestions
import fr.gouv.monprojetsup.suggestions.dto.GetExplanationsAndExamplesServiceDTO
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO
import fr.gouv.monprojetsup.suggestions.server.domain.port.Suggestions2Service
import fr.gouv.monprojetsup.suggestions.server.domain.port.Suggestions2ServiceRequest
import org.springframework.stereotype.Service


@Service
class ExplanationsService(
    private val algo: AlgoSuggestions,
    private val suggestions2Service: Suggestions2Service
    ) {

    fun getExplanations(
        profil: ProfileDTO,
        keys: List<String>,
        withDetails: Boolean
    ): List<GetExplanationsAndExamplesServiceDTO.ExplanationAndExamples>? {
        val explanationsNaiveBayes = suggestions2Service.recupererLesExplications(
            Suggestions2ServiceRequest(
                profile = profil,
                keys = keys)
        )
        return algo.getExplanationsAndExamples(
            profil,
            keys,
            explanationsNaiveBayes,
            withDetails
        )
    }

}