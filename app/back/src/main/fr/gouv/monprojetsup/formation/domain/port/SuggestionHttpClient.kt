package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionEtExemplesMetiers
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil

interface SuggestionHttpClient {
    @Throws(MonProjetSupInternalErrorException::class)
    fun recupererLesSuggestions(profilEleve: ProfilEleve.AvecProfilExistant): SuggestionsPourUnProfil

    @Throws(MonProjetSupInternalErrorException::class, MonProjetSupIllegalStateErrorException::class)
    fun recupererLesExplications(
        profilEleve: ProfilEleve.AvecProfilExistant,
        idsFormations: List<String>,
    ): Map<String, ExplicationsSuggestionEtExemplesMetiers?>
}
