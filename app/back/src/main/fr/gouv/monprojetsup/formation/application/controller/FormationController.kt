package fr.gouv.monprojetsup.formation.application.controller

import fr.gouv.monprojetsup.formation.application.dto.FormationAvecExplicationsDTO
import fr.gouv.monprojetsup.formation.application.dto.FormationsAvecExplicationsDTO
import fr.gouv.monprojetsup.formation.application.dto.ProfilObligatoireRequeteDTO
import fr.gouv.monprojetsup.formation.application.dto.ProfilOptionnelRequeteDTO
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.usecase.RecupererFormationService
import fr.gouv.monprojetsup.formation.usecase.SuggestionsFormationsService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("api/v1/formations")
@RestController
class FormationController(
    val suggestionsFormationsService: SuggestionsFormationsService,
    val recupererFormationService: RecupererFormationService,
) {
    @PostMapping("/suggestions")
    fun postSuggestions(
        @RequestBody suggestionsFormationsRequeteDTO: ProfilObligatoireRequeteDTO,
    ): FormationsAvecExplicationsDTO {
        val formationsPourProfil: List<FicheFormation.FicheFormationPourProfil> =
            suggestionsFormationsService.suggererFormations(
                profilEleve = suggestionsFormationsRequeteDTO.profil.toProfil(),
                deLIndex = 0,
                aLIndex = NOMBRE_FORMATIONS_SUGGEREES,
            )
        return FormationsAvecExplicationsDTO(
            formations = formationsPourProfil.map { FormationAvecExplicationsDTO(it) },
        )
    }

    @PostMapping("/{idformation}")
    fun postFormation(
        @PathVariable("idformation") idFormation: String,
        @RequestBody profilOptionnelRequeteDTO: ProfilOptionnelRequeteDTO,
    ): FormationAvecExplicationsDTO {
        val ficheFormation =
            recupererFormationService.recupererFormation(
                profilEleve = profilOptionnelRequeteDTO.profil?.toProfil(),
                idFormation = idFormation,
            )
        return FormationAvecExplicationsDTO(ficheFormation)
    }

    @PostMapping("/recherche")
    fun postRecherche(
        @RequestParam recherche: String,
        @RequestBody profilOptionnelRequeteDTO: ProfilOptionnelRequeteDTO,
    ): FormationsAvecExplicationsDTO {
        return FormationsAvecExplicationsDTO(
            formations = emptyList(),
        )
    }

    @PostMapping("/favoris")
    fun postFavoris(
        @RequestBody profilOptionnelRequeteDTO: ProfilObligatoireRequeteDTO,
    ): FormationsAvecExplicationsDTO {
        return FormationsAvecExplicationsDTO(
            formations = emptyList(),
        )
    }

    companion object {
        private const val NOMBRE_FORMATIONS_SUGGEREES = 50
    }
}
