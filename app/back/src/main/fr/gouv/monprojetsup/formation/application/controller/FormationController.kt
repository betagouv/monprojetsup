package fr.gouv.monprojetsup.formation.application.controller

import fr.gouv.monprojetsup.authentification.application.controller.AuthentifieController
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilConnecte
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve.Identifie
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve.Inconnu
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEnseignant
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.hateoas.domain.PaginationConstants.NUMERO_PREMIERE_PAGE
import fr.gouv.monprojetsup.commun.hateoas.domain.PaginationConstants.PARAMETRE_NUMERO_PAGE
import fr.gouv.monprojetsup.commun.hateoas.usecase.HateoasBuilder
import fr.gouv.monprojetsup.formation.application.dto.FormationAvecExplicationsDTO
import fr.gouv.monprojetsup.formation.application.dto.FormationCourteDTO
import fr.gouv.monprojetsup.formation.application.dto.FormationsAvecExplicationsDTO
import fr.gouv.monprojetsup.formation.application.dto.FormationsCourtesDTO
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.usecase.RechercherFormationsService
import fr.gouv.monprojetsup.formation.usecase.RecupererFormationService
import fr.gouv.monprojetsup.formation.usecase.RecupererFormationsService
import fr.gouv.monprojetsup.formation.usecase.SuggestionsFormationsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/formations")
@Tag(name = "Formation", description = "API des formations proposées sur MonProjetSup")
class FormationController(
    val suggestionsFormationsService: SuggestionsFormationsService,
    val recupererFormationService: RecupererFormationService,
    val recupererFormationsService: RecupererFormationsService,
    val rechercherFormation: RechercherFormationsService,
    val hateoasBuilder: HateoasBuilder,
) : AuthentifieController() {
    @GetMapping("/suggestions")
    @Operation(summary = "Récupérer les suggestions de formations pour un profil d'élève")
    fun getSuggestionsFormations(
        @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "1", value = PARAMETRE_NUMERO_PAGE) numeroDePage: Int,
    ): FormationsAvecExplicationsDTO {
        if (numeroDePage < NUMERO_PREMIERE_PAGE) {
            throw MonProjetSupBadRequestException("PAGINATION_COMMENCE_A_1", "La pagination commence à 1")
        }
        val profilEleve = recupererEleveIdentifie()
        val suggestions = suggestionsFormationsService.recupererToutesLesSuggestionsPourUnProfil(profilEleve)
        val hateoas =
            hateoasBuilder.creerHateoas(
                liste = suggestions.formations,
                numeroDePageActuelle = numeroDePage,
                tailleLot = TAILLE_LOT_SUGGESTIONS_FORMATIONS,
            )
        val formationsSuggerees =
            recupererFormationsService.recupererFichesFormationPourProfil(
                profilEleve = profilEleve,
                suggestionsPourUnProfil = suggestions,
                idsFormations = hateoas.listeCoupee.map { it.idFormation },
            )
        val dto = FormationsAvecExplicationsDTO(formations = formationsSuggerees.map { FormationAvecExplicationsDTO(it) })
        dto.ajouterHateoas(hateoas)
        return dto
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
        @Parameter(description = "Formation recherchée") @RequestParam recherche: String,
        @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "1", value = PARAMETRE_NUMERO_PAGE) numeroDePage: Int,
    ): FormationsCourtesDTO {
        val formationRecherchees = recupererLesFormationsAssocieesALaRecherche(recherche)
        val formationsFiltrees = filtreesFormationPourEleve(formationRecherchees)
        val hateoas =
            hateoasBuilder.creerHateoas(
                liste = formationsFiltrees,
                numeroDePageActuelle = numeroDePage,
                tailleLot = TAILLE_LOT_RECHERCHE_SUCCINCTE,
            )
        val dto = FormationsCourtesDTO(formations = hateoas.listeCoupee.map { FormationCourteDTO(it) })
        dto.ajouterHateoas(hateoas)
        return dto
    }

    @GetMapping("/recherche/detaillee")
    fun getRechercheFormationDetaillee(
        @Parameter(description = "Formation recherchée") @RequestParam recherche: String,
        @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "1", value = PARAMETRE_NUMERO_PAGE) numeroDePage: Int,
    ): FormationsAvecExplicationsDTO {
        val idsFormationRecherchees = recupererLesFormationsAssocieesALaRecherche(recherche).map { it.id }
        val dto =
            recupererUneListeDeFormationsDetaillees(
                ids = idsFormationRecherchees,
                numeroDePage = numeroDePage,
                tailleLot = TAILLE_LOT_FORMATIONS,
            )
        return dto
    }

    @GetMapping
    fun getFormations(
        @RequestParam ids: List<String>,
        @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "1", value = PARAMETRE_NUMERO_PAGE) numeroDePage: Int,
    ): FormationsAvecExplicationsDTO {
        return recupererUneListeDeFormationsDetaillees(ids = ids, numeroDePage = numeroDePage, tailleLot = TAILLE_LOT_RECHERCHE_DETAILLEE)
    }

    private fun filtreesFormationPourEleve(formationRecherchees: List<FormationCourte>): List<FormationCourte> {
        val formationsFiltrees =
            when (val utilisateur = recupererUtilisateur()) {
                is Identifie -> formationRecherchees.filterNot { formation -> utilisateur.corbeilleFormations.any { it == formation.id } }
                else -> formationRecherchees
            }
        return formationsFiltrees
    }

    private fun recupererUneListeDeFormationsDetaillees(
        ids: List<String>,
        numeroDePage: Int,
        tailleLot: Int,
    ): FormationsAvecExplicationsDTO {
        val profilEleve = recupererEleveIdentifie()
        val hateoas =
            hateoasBuilder.creerHateoas(
                liste = ids,
                numeroDePageActuelle = numeroDePage,
                tailleLot = tailleLot,
            )
        val toutesLesSuggestions = suggestionsFormationsService.recupererToutesLesSuggestionsPourUnProfil(profilEleve)
        val formations =
            recupererFormationsService.recupererFichesFormationPourProfil(
                profilEleve = profilEleve,
                suggestionsPourUnProfil = toutesLesSuggestions,
                idsFormations = hateoas.listeCoupee,
            )
        val dto = FormationsAvecExplicationsDTO(formations = formations.map { FormationAvecExplicationsDTO(it) })
        dto.ajouterHateoas(hateoas)
        return dto
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
        val formationRecherchees =
            rechercherFormation.rechercheLesFormationsCorrespondantes(
                recherche = recherche,
                tailleMinimumRecherche = TAILLE_MINIMUM_RECHERCHE,
            )
        return formationRecherchees
    }

    companion object {
        private const val TAILLE_LOT_RECHERCHE_DETAILLEE = 30
        private const val TAILLE_LOT_RECHERCHE_SUCCINCTE = 30
        private const val TAILLE_LOT_SUGGESTIONS_FORMATIONS = 30
        private const val TAILLE_LOT_FORMATIONS = 30
        private const val TAILLE_MINIMUM_RECHERCHE = 2
        private const val TAILLE_MAXIMAL_RECHERCHE = 50
    }
}
