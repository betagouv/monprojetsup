package fr.gouv.monprojetsup.formation.application.controller

import fr.gouv.monprojetsup.authentification.application.controller.AuthentifieOuPasController
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve.AvecProfilExistant
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.hateoas.domain.PaginationConstants.NUMERO_PREMIERE_PAGE
import fr.gouv.monprojetsup.commun.hateoas.domain.PaginationConstants.PARAMETRE_NUMERO_PAGE
import fr.gouv.monprojetsup.commun.hateoas.usecase.HateoasBuilder
import fr.gouv.monprojetsup.formation.application.dto.FormationAvecExplicationsDTO
import fr.gouv.monprojetsup.formation.application.dto.FormationCourteDTO
import fr.gouv.monprojetsup.formation.application.dto.FormationsAvecExplicationsDTO
import fr.gouv.monprojetsup.formation.application.dto.FormationsCourtesDTO
import fr.gouv.monprojetsup.formation.application.dto.GetFichesFormationsDTO
import fr.gouv.monprojetsup.formation.application.dto.GetFormationDTO
import fr.gouv.monprojetsup.formation.application.dto.GetSuggestionsDTO
import fr.gouv.monprojetsup.formation.application.dto.RechercheFormationsDTO
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.usecase.OrdonnerRechercheFormationsBuilder
import fr.gouv.monprojetsup.formation.usecase.RechercherFormationsService
import fr.gouv.monprojetsup.formation.usecase.RecupererFicheFormationService
import fr.gouv.monprojetsup.formation.usecase.RecupererFichesFormationsService
import fr.gouv.monprojetsup.formation.usecase.RecupererFormationsService
import fr.gouv.monprojetsup.formation.usecase.SuggestionsFormationsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/public/formations")
@Tag(name = "Formation", description = "API des formations proposées sur MonProjetSup")
class FormationController(
    val suggestionsFormationsService: SuggestionsFormationsService,
    val recupererFicheFormationService: RecupererFicheFormationService,
    val recupererFichesFormationsService: RecupererFichesFormationsService,
    val recupererFormationsService: RecupererFormationsService,
    val rechercherFormation: RechercherFormationsService,
    val ordonnerRechercheFormationsBuilder: OrdonnerRechercheFormationsBuilder,
    val hateoasBuilder: HateoasBuilder,
) : AuthentifieOuPasController() {
    @PostMapping("/suggestions")
    @Operation(
        summary = "Récupère les suggestions de formations",
        description =
            "Récupère les suggestions de formations pour un profil d'élève. Chaque suggestion s'accompagne des " +
                "informations nécessaires à l'affichage de la fiche formation, y compris la liste des explications sur la raison de " +
                "cette suggestion. Un lien permet la pagination des résultats.",
    )
    fun getSuggestionsFormations(
        @RequestBody request: GetSuggestionsDTO,
    ): FormationsAvecExplicationsDTO {
        if (request.numeroDePage < NUMERO_PREMIERE_PAGE) {
            throw MonProjetSupBadRequestException(
                code = "PAGE_INVALIDE",
                msg = "La pagination commence à $NUMERO_PREMIERE_PAGE",
            )
        }
        val profilEleve =
            when {
                request.profil == null -> recupererEleveAvecProfilExistant() ?: AvecProfilExistant("")
                else -> request.profil.toProfilExistant()
            }
        val suggestions = suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(profilEleve)
        val hateoas =
            hateoasBuilder.creerHateoas(
                liste = suggestions.formations,
                numeroDePageActuelle = request.numeroDePage,
                tailleLot = TAILLE_LOT_SUGGESTIONS_FORMATIONS,
            )

        val formationsSuggerees: List<FicheFormation.FicheFormationPourProfil> =
            recupererFichesFormationsService.recupererFichesFormationPourProfil(
                profilEleve = profilEleve,
                suggestionsPourUnProfil = suggestions,
                idsFormations = hateoas.listeCoupee.map { it.idFormation },
                obsoletesInclus = false,
            )
        return FormationsAvecExplicationsDTO(
            formations =
                formationsSuggerees.filterNot { formation -> profilEleve.corbeilleFormations.any { it == formation.id } }
                    .map<FicheFormation, FormationAvecExplicationsDTO> { FormationAvecExplicationsDTO(it) },
        )
    }

    @PostMapping("/recherche/succincte")
    @Operation(
        summary = "Recherche de formation, mode succint",
        description =
            "A partir du contenu de la barre de recherche, récupérer la liste des formations associées à cette " +
                "recherche, et le lien de pagination.",
    )
    fun getRechercheFormationSuccincte(
        @RequestBody request: RechercheFormationsDTO,
    ): FormationsCourtesDTO {
        val profilEleve =
            when {
                request.profil == null -> recupererEleveAvecProfilExistant() ?: AvecProfilExistant("")
                else -> request.profil.toProfilExistant()
            }
        val suggestionsPourLeProfil = suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(profilEleve)

        val formationsCourtesTriees =
            getListeFormationsCourtesTriees(
                request.recherche,
                suggestionsPourLeProfil,
                request.numeroDePage,
            )
        return FormationsCourtesDTO(formations = formationsCourtesTriees.map { FormationCourteDTO(it) })
    }

    @PostMapping("/recherche/detaillee")
    @Operation(
        summary = "Recherche de formation, mode détaillé",
        description =
            "A partir du contenu de la barre de recherche, récupère la liste des formations associées à cette recherche, " +
                "et toutes les informations nécessaires à l'affichage des fiches formations correspondantes, y compris la liste des " +
                "explications sur la raison de cette suggestion.",
    )
    fun getRechercheFormationDetaillee(
        @RequestBody request: RechercheFormationsDTO,
    ): FormationsAvecExplicationsDTO {
        val profilEleve =
            when {
                request.profil == null -> recupererEleveAvecProfilExistant() ?: AvecProfilExistant("")
                else -> request.profil.toProfilExistant()
            }
        val suggestionsPourLeProfil = suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(profilEleve)

        val formationsCourtesTriees =
            getListeFormationsCourtesTriees(
                request.recherche,
                suggestionsPourLeProfil,
                request.numeroDePage,
            )

        val formations =
            recupererFichesFormationsService.recupererFichesFormationPourProfil(
                profilEleve = profilEleve,
                suggestionsPourUnProfil = suggestionsPourLeProfil,
                idsFormations = formationsCourtesTriees.map { it.id },
                obsoletesInclus = false,
            )

        return FormationsAvecExplicationsDTO(
            formations =
                formations.map {
                    FormationAvecExplicationsDTO(
                        it,
                    )
                },
        )
    }

    private fun getListeFormationsCourtesTriees(
        recherche: String,
        suggestionsPourLeProfil: SuggestionsPourUnProfil,
        numeroDePage: Int,
    ): List<FormationCourte> {
        val resultatRecherche = recupererLesFormationsAssocieesALaRecherche(recherche)

        val formationRechercheesTriees =
            ordonnerRechercheFormationsBuilder.trierParScoreEtSelonSuggestionsProfil(
                resultats = resultatRecherche,
                formationsAvecLeurAffinite = suggestionsPourLeProfil.formations,
            )
        val hateoas =
            hateoasBuilder.creerHateoas(
                liste = formationRechercheesTriees,
                numeroDePageActuelle = numeroDePage,
                tailleLot = TAILLE_LOT_RECHERCHE,
            )
        return hateoas.listeCoupee
    }

    @PostMapping
    @Operation(
        summary = "Récupération d'une formation, mode détaillé",
        description =
            "A partir de l'identifiant d'une fiche formation, récupère toutes les informations nécessaires à l'affichage de " +
                "la fiche formation, y compris la liste des explications sur la raison de cette suggestion.",
    )
    fun getFormation(
        @RequestBody request: GetFormationDTO,
    ): FormationAvecExplicationsDTO {
        val profilEleve =
            when {
                request.profil == null -> recupererEleveAvecProfilExistant() ?: AvecProfilExistant("")
                else -> request.profil.toProfilExistant()
            }
        val ficheFormation = recupererFicheFormationService.recupererFormation(profilEleve = profilEleve, idFormation = request.id)
        return FormationAvecExplicationsDTO(ficheFormation)
    }

    @PostMapping("/fiches")
    @Operation(
        summary = "Récupération d'une liste de fiches formations",
        description =
            "A partir d'une liste d'ids, récupère toutes les informations nécessaires à l'affichage des fiches formations, " +
                "y compris la liste des explications sur la raison de cette suggestion, plus un lien de pagination." +
                "Ces informations sont personnalisées en fonction du profil transmis",
    )
    fun getFichesFormations(
        @RequestBody request: GetFichesFormationsDTO,
    ): FormationsAvecExplicationsDTO {
        val hateoas =
            hateoasBuilder.creerHateoas(
                liste = request.ids,
                numeroDePageActuelle = request.numeroDePage,
                tailleLot = TAILLE_LOT_FORMATIONS,
            )
        val profilEleve =
            when {
                request.profil == null -> recupererEleveAvecProfilExistant() ?: AvecProfilExistant("")
                else -> request.profil.toProfilExistant()
            }

        val suggestions = suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(profilEleve)
        val formations =
            recupererFichesFormationsService.recupererFichesFormationPourProfil(
                profilEleve = profilEleve,
                suggestionsPourUnProfil = suggestions,
                idsFormations = hateoas.listeCoupee,
                obsoletesInclus = true,
            )
        return FormationsAvecExplicationsDTO(
            formations =
                formations.map<FicheFormation, FormationAvecExplicationsDTO> {
                    FormationAvecExplicationsDTO(
                        it,
                    )
                },
        )
    }

    @GetMapping
    @Operation(
        summary = "Récupération d'une liste de formations",
        description = "A partir d'une liste d'ids, récupère l'id et le nom des formations, plus un lien de pagination.",
    )
    fun getFormations(
        @RequestParam ids: List<String>,
        @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "1", value = PARAMETRE_NUMERO_PAGE) numeroDePage: Int,
    ): FormationsCourtesDTO {
        val hateoas =
            hateoasBuilder.creerHateoas(
                liste = ids,
                numeroDePageActuelle = numeroDePage,
                tailleLot = TAILLE_LOT_FORMATIONS,
            )
        val formations = recupererFormationsService.recupererFormations(idsFormations = hateoas.listeCoupee)
        return FormationsCourtesDTO(formations = formations.map { FormationCourteDTO(it) })
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun recupererLesFormationsAssocieesALaRecherche(recherche: String): Map<FormationCourte, Double> {
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
            rechercherFormation.rechercheLesFormationsAvecLeurScoreCorrespondantes(
                recherche = recherche,
                tailleMinimumRecherche = TAILLE_MINIMUM_RECHERCHE,
            )

        return formationRecherchees
    }

    companion object {
        private const val TAILLE_LOT_RECHERCHE = 30
        private const val TAILLE_LOT_SUGGESTIONS_FORMATIONS = 30
        private const val TAILLE_LOT_FORMATIONS = 30
        internal const val TAILLE_MINIMUM_RECHERCHE = 2
        private const val TAILLE_MAXIMAL_RECHERCHE = 150
    }
}
