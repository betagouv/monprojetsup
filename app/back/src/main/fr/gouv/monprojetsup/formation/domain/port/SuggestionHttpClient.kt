package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestion
import fr.gouv.monprojetsup.formation.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil

interface SuggestionHttpClient {
    @Throws(MonProjetSupInternalErrorException::class)
    fun recupererLesSuggestions(profilEleve: ProfilEleve): SuggestionsPourUnProfil

    @Throws(MonProjetSupInternalErrorException::class, MonProjetIllegalStateErrorException::class)
    fun recupererLesExplications(
        profilEleve: ProfilEleve,
        idsFormations: List<String>,
    ): Map<String, ExplicationsSuggestion?>
}
