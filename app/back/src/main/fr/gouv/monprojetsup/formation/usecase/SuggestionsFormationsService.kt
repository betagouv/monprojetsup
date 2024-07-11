package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil.FormationAvecSonAffinite
import fr.gouv.monprojetsup.formation.domain.port.SuggestionHttpClient
import org.springframework.stereotype.Service
import kotlin.jvm.Throws
import kotlin.math.min

@Service
class SuggestionsFormationsService(
    val suggestionHttpClient: SuggestionHttpClient,
    val recupererFormationsService: RecupererFormationsService,
) {
    @Throws(MonProjetSupInternalErrorException::class)
    fun suggererFormations(
        profilEleve: ProfilEleve,
        deLIndex: Int,
        aLIndex: Int,
    ): List<FicheFormation.FicheFormationPourProfil> {
        val affinitesFormationEtMetier = suggestionHttpClient.recupererLesSuggestions(profilEleve)
        val idsDesFormationsTries = recupererLesIdsDesFormationsTries(affinitesFormationEtMetier.formations, deLIndex, aLIndex)
        return if (idsDesFormationsTries.isEmpty()) {
            emptyList()
        } else {
            recupererFormationsService.recupererFichesFormationPourProfil(
                profilEleve = profilEleve,
                suggestionsPourUnProfil = affinitesFormationEtMetier,
                idsFormations = idsDesFormationsTries,
            )
        }
    }

    @Throws(MonProjetSupInternalErrorException::class)
    fun recupererToutesLesSuggestionsPourUnProfil(profilEleve: ProfilEleve): SuggestionsPourUnProfil {
        return suggestionHttpClient.recupererLesSuggestions(profilEleve)
    }

    private fun recupererLesIdsDesFormationsTries(
        formationsAvecAffinite: List<FormationAvecSonAffinite>,
        deLIndex: Int,
        aLIndex: Int,
    ): List<String> {
        val nombreDeFormations = formationsAvecAffinite.size
        if (deLIndex > nombreDeFormations) return emptyList()
        return formationsAvecAffinite.sortedByDescending {
            it.tauxAffinite
        }.subList(deLIndex, min(aLIndex, formationsAvecAffinite.size)).map { it.idFormation }
    }
}
