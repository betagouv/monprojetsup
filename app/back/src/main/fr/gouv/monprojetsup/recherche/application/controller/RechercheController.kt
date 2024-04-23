package fr.gouv.monprojetsup.recherche.application.controller

import fr.gouv.monprojetsup.recherche.application.dto.FormationDTO
import fr.gouv.monprojetsup.recherche.application.dto.RechercheFormationReponseDTO
import fr.gouv.monprojetsup.recherche.application.dto.RechercheFormationRequeteDTO
import fr.gouv.monprojetsup.recherche.usecase.SuggestionsFormationsService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("api/v1/formations")
@RestController
class RechercheController(
    val suggestionsFormationsService: SuggestionsFormationsService,
) {
    @PostMapping("/recherche")
    fun postRecherche(
        @RequestBody rechercheFormationRequeteDTO: RechercheFormationRequeteDTO,
    ): RechercheFormationReponseDTO {
        val formationsPourProfil = suggestionsFormationsService.suggererFormations(rechercheFormationRequeteDTO.profile.toProfile())
        return RechercheFormationReponseDTO(
            formationsPourProfil.map { formationPourProfil ->
                FormationDTO(
                    id = formationPourProfil.id,
                    nom = formationPourProfil.nom,
                    tauxAffinite = formationPourProfil.tauxAffinite,
                    villes = formationPourProfil.communesTrieesParAffinites,
                    metiers = formationPourProfil.metiersTriesParAffinites,
                )
            },
        )
    }
}