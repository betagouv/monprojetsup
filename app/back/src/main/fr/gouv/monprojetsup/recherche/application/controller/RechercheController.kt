package fr.gouv.monprojetsup.recherche.application.controller

import fr.gouv.monprojetsup.recherche.application.dto.ExplicationsDTO
import fr.gouv.monprojetsup.recherche.application.dto.FormationDTO
import fr.gouv.monprojetsup.recherche.application.dto.FormationDetailleDTO
import fr.gouv.monprojetsup.recherche.application.dto.RechercheFormationReponseDTO
import fr.gouv.monprojetsup.recherche.application.dto.RechercheFormationRequeteDTO
import fr.gouv.monprojetsup.recherche.application.dto.RecupererFormationReponseDTO
import fr.gouv.monprojetsup.recherche.application.dto.RecupererFormationRequeteDTO
import fr.gouv.monprojetsup.recherche.usecase.RecupererFormationService
import fr.gouv.monprojetsup.recherche.usecase.SuggestionsFormationsService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("api/v1/formations")
@RestController
class RechercheController(
    val suggestionsFormationsService: SuggestionsFormationsService,
    val recupererFormationService: RecupererFormationService,
) {
    @PostMapping("/recherche")
    fun postRecherche(
        @RequestBody rechercheFormationRequeteDTO: RechercheFormationRequeteDTO,
    ): RechercheFormationReponseDTO {
        val formationsPourProfil =
            suggestionsFormationsService.suggererFormations(
                profilEleve = rechercheFormationRequeteDTO.profil.toProfil(),
                deLIndex = 0,
                aLIndex = NOMBRE_FORMATIONS_SUGGEREES,
            )
        return RechercheFormationReponseDTO(
            formations =
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

    @PostMapping("/{idformation}")
    fun postFormation(
        @PathVariable("idformation") idFormation: String,
        @RequestBody recupererFormationRequeteDTO: RecupererFormationRequeteDTO,
    ): RecupererFormationReponseDTO {
        val formation =
            recupererFormationService.recupererFormation(
                profilEleve = recupererFormationRequeteDTO.profil?.toProfil(),
                idFormation = idFormation,
            )
        return RecupererFormationReponseDTO(
            formation = FormationDetailleDTO.fromFicheFormation(formation),
            explications = formation.explications?.let { explications -> ExplicationsDTO.fromExplications(explications) },
        )
    }

    companion object {
        private const val NOMBRE_FORMATIONS_SUGGEREES = 50
    }
}
