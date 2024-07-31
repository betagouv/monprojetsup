package fr.gouv.monprojetsup.formation.application.controller

import fr.gouv.monprojetsup.authentification.application.controller.AuthentifieController
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilConnecte
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve.Identifie
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve.Inconnu
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEnseignant
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.formation.application.dto.FormationAvecExplicationsDTO
import fr.gouv.monprojetsup.formation.application.dto.FormationCourteDTO
import fr.gouv.monprojetsup.formation.application.dto.FormationsAvecExplicationsDTO
import fr.gouv.monprojetsup.formation.application.dto.FormationsCourtesDTO
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.usecase.RechercherFormationsService
import fr.gouv.monprojetsup.formation.usecase.RecupererFormationService
import fr.gouv.monprojetsup.formation.usecase.RecupererFormationsService
import fr.gouv.monprojetsup.formation.usecase.SuggestionsFormationsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.Throws

@RestController
@RequestMapping("api/v1/formations")
@Tag(name = "Formation", description = "API des formations proposées sur MonProjetSup")
class FormationController(
    val suggestionsFormationsService: SuggestionsFormationsService,
    val recupererFormationService: RecupererFormationService,
    val recupererFormationsService: RecupererFormationsService,
    val rechercherFormation: RechercherFormationsService,
) : AuthentifieController() {
    @GetMapping("/suggestions")
    @Operation(summary = "Récupérer les suggestions de formations pour un profil d'élève")
    fun getSuggestionsFormations(): FormationsAvecExplicationsDTO {
        val formationsPourProfil: List<FicheFormation.FicheFormationPourProfil> =
            suggestionsFormationsService.suggererFormations(
                profilEleve = recupererEleveIdentifie(),
                deLIndex = 0,
                aLIndex = NOMBRE_FORMATIONS_SUGGEREES,
            )
        return FormationsAvecExplicationsDTO(
            formations = formationsPourProfil.map { FormationAvecExplicationsDTO(it) },
        )
    }

    @GetMapping("/{idformation}")
    fun getFormation(
        @PathVariable("idformation") idFormation: String,
    ): FormationAvecExplicationsDTO {
        val profil =
            when (val utilisateur = recupererUtilisateur()) {
                is Identifie -> utilisateur
                is Inconnu, ProfilEnseignant, ProfilConnecte -> null
            }
        val ficheFormation = recupererFormationService.recupererFormation(profilEleve = profil, idFormation = idFormation)
        return FormationAvecExplicationsDTO(ficheFormation)
    }

    @GetMapping("/recherche/succincte")
    fun getRechercheFormationSuccincte(
        @RequestParam recherche: String,
    ): FormationsCourtesDTO {
        val formationRecherchees = recupererLesFormationsAssocieesALaRecherche(recherche)
        return FormationsCourtesDTO(
            formationRecherchees.map { FormationCourteDTO(it) },
        )
    }

    @GetMapping("/recherche/detaillee")
    fun getRechercheFormationDetaillee(
        @RequestParam recherche: String,
    ): FormationsAvecExplicationsDTO {
        val formationRecherchees = recupererLesFormationsAssocieesALaRecherche(recherche)
        return recupererUneListeDeFormationsDetaillees(formationRecherchees.map { it.id })
    }

    @GetMapping
    fun getFormations(
        @RequestParam ids: List<String>,
    ): FormationsAvecExplicationsDTO {
        return recupererUneListeDeFormationsDetaillees(ids)
    }

    private fun recupererUneListeDeFormationsDetaillees(ids: List<String>): FormationsAvecExplicationsDTO {
        val profilEleve = recupererEleveIdentifie()
        val toutesLesSuggestions = suggestionsFormationsService.recupererToutesLesSuggestionsPourUnProfil(profilEleve)
        val formations = recupererFormationsService.recupererFichesFormationPourProfil(profilEleve, toutesLesSuggestions, ids)
        return FormationsAvecExplicationsDTO(formations = formations.map { FormationAvecExplicationsDTO(it) })
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun recupererLesFormationsAssocieesALaRecherche(recherche: String): List<FormationCourte> {
        if (recherche.length > TAILLE_MAXIMAL_RECHERCHE) {
            throw MonProjetSupBadRequestException(
                code = "REQUETE_TROP_LONGUE",
                msg = "La taille de la requête dépasse la taille maximale de $TAILLE_MAXIMAL_RECHERCHE caractères",
            )
        } else if (recherche.length < TAILLE_MINIMUM_RECHERCHE) {
            throw MonProjetSupBadRequestException(
                code = "REQUETE_TROP_COURTE",
                msg = "La taille de la requête est trop courte. Elle doit faire au moins $TAILLE_MINIMUM_RECHERCHE caractères",
            )
        }
        val formationRecherchees = rechercherFormation.rechercheLesFormationsCorrespondantes(recherche)
        return formationRecherchees
    }

    companion object {
        private const val NOMBRE_FORMATIONS_SUGGEREES = 30
        const val TAILLE_MINIMUM_RECHERCHE = 2
        private const val TAILLE_MAXIMAL_RECHERCHE = 50
    }
}
