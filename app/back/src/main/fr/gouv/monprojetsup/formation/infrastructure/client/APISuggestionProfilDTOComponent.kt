package fr.gouv.monprojetsup.formation.infrastructure.client

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.infrastructure.dto.APISuggestionProfilDTO
import fr.gouv.monprojetsup.referentiel.domain.port.InteretRepository
import fr.gouv.monprojetsup.referentiel.domain.port.SpecialitesRepository
import org.springframework.stereotype.Component

@Component
class APISuggestionProfilDTOComponent(
    private val specialitesRepository: SpecialitesRepository,
    private val interetRepository: InteretRepository,
) {
    fun creerAPISuggestionProfilDTO(profilEleve: ProfilEleve.Identifie): APISuggestionProfilDTO {
        val specialites =
            profilEleve.specialites?.takeUnless { it.isEmpty() }?.let {
                specialitesRepository.recupererLesSpecialites(it).map { specialite -> specialite.label }
            }
        val centresInterets =
            profilEleve.centresInterets?.takeUnless { it.isEmpty() }?.let { centresInterets ->
                interetRepository.recupererLesInteretsDeSousCategories(centresInterets).map { interet -> interet.id }
            }
        val apiSuggestionProfilDTO =
            APISuggestionProfilDTO(
                profilEleve = profilEleve,
                specialites = specialites,
                centresInterets = centresInterets,
            )
        return apiSuggestionProfilDTO
    }
}
