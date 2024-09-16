package fr.gouv.monprojetsup.formation.infrastructure.client

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.infrastructure.dto.APISuggestionProfilDTO
import org.springframework.stereotype.Component

@Component
class APISuggestionProfilDTOComponent {
    fun creerAPISuggestionProfilDTO(profilEleve: ProfilEleve.Identifie): APISuggestionProfilDTO {
        val specialites = profilEleve.specialites?.takeUnless { it.isEmpty() }
        val centresInterets = profilEleve.centresInterets?.takeUnless { it.isEmpty() }
        val apiSuggestionProfilDTO =
            APISuggestionProfilDTO(
                profilEleve = profilEleve,
                specialites = specialites,
                centresInterets = centresInterets,
            )
        return apiSuggestionProfilDTO
    }
}
