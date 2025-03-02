package fr.gouv.monprojetsup.suggestions.server.usecase

import fr.gouv.monprojetsup.suggestions.algo.AlgoSuggestions
import fr.gouv.monprojetsup.suggestions.dto.GetExplanationsAndExamplesServiceDTO
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO
import fr.gouv.monprojetsup.suggestions.server.domain.port.Suggestions2Service
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service


@Service
class ExplanationsService(
    private val algo: AlgoSuggestions,
    private val suggestions2Service: Suggestions2Service,
    @Value("\${mps.generateDetailedExplanations}")
    val generateDetailedExplanations: Boolean,
    ) {
    //after spring boot initialization, inject the value of generateDetailedExplanationsinto algo
    @PostConstruct
    fun afterInit() {
        algo.setGenerateDetailedExplanations(generateDetailedExplanations)
    }

    fun getExplanations(
        profil: ProfileDTO,
        keys: List<String>
    ): List<GetExplanationsAndExamplesServiceDTO.ExplanationAndExamples>? {
        val explanationsNaiveBayes = suggestions2Service.recupererLesExplications(profil, keys)
        return algo.getExplanationsAndExamples(
            profil,
            keys,
            explanationsNaiveBayes
        )
    }

}