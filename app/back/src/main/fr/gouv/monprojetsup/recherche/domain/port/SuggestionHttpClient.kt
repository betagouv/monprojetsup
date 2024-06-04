package fr.gouv.monprojetsup.recherche.domain.port

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.recherche.domain.entity.AffinitesPourProfil
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationsSuggestion
import fr.gouv.monprojetsup.recherche.domain.entity.ProfilEleve

interface SuggestionHttpClient {
    @Throws(MonProjetSupInternalErrorException::class)
    fun recupererLesAffinitees(profilEleve: ProfilEleve): AffinitesPourProfil

    @Throws(MonProjetSupInternalErrorException::class, MonProjetIllegalStateErrorException::class)
    fun recupererLesExplications(
        profilEleve: ProfilEleve,
        idFormation: String,
    ): ExplicationsSuggestion
}
