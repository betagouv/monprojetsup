package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.domain.port.SuggestionHttpClient
import org.springframework.stereotype.Service

@Service
class SuggestionsFormationsService(
    val suggestionHttpClient: SuggestionHttpClient,
) {
    @Throws(MonProjetSupInternalErrorException::class)
    fun recupererLesSuggestionsPourUnProfil(profilEleve: ProfilEleve.Identifie): SuggestionsPourUnProfil {
        return suggestionHttpClient.recupererLesSuggestions(profilEleve)
    }
}
