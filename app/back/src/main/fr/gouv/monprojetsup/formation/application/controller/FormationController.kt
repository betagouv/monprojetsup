package fr.gouv.monprojetsup.formation.application.controller

import fr.gouv.monprojetsup.authentification.application.controller.AuthentifieOuPasController
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve.AvecProfilExistant
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.hateoas.domain.PaginationConstants.PARAMETRE_NUMERO_PAGE
import fr.gouv.monprojetsup.commun.hateoas.domain.entity.Hateoas
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
        val profilEleve = when{
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
        return creerFormationsAvecExplicationsDTO(
            formations = formationsSuggerees.filterNot { formation -> profilEleve.corbeilleFormations.any { it == formation.id } },
            hateoas = hateoas,
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

        val resultatRecherche = recupererLesFormationsAssocieesALaRecherche(request.recherche)

        val profilEleve = when{
            request.profil == null -> recupererEleveAvecProfilExistant() ?: AvecProfilExistant("")
            else -> request.profil.toProfilExistant()
        }

        val formationsTriees = ordonnerRechercheFormationsBuilder.trierParScoreEtSelonSuggestionsProfil(
            resultats = resultatRecherche,
            formationsAvecLeurAffinite =
            suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(
                profilEleve,
            ).formations,
        )
        val hateoas =
            hateoasBuilder.creerHateoas(
                liste = formationsTriees,
                numeroDePageActuelle = request.numeroDePage,
                tailleLot = TAILLE_LOT_RECHERCHE_SUCCINCTE,
            )
        val dto = FormationsCourtesDTO(formations = hateoas.listeCoupee.map { FormationCourteDTO(it) })
        dto.ajouterHateoas(hateoas)
        return dto
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
        val resultatRecherche = recupererLesFormationsAssocieesALaRecherche(request.recherche)

        val profilEleve = when{
            request.profil == null -> recupererEleveAvecProfilExistant() ?: AvecProfilExistant("")
            else -> request.profil.toProfilExistant()
        }
        val suggestions = suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(profilEleve)
        val formationRechercheesTriees = ordonnerRechercheFormationsBuilder.trierParScoreEtSelonSuggestionsProfil(
            resultats = resultatRecherche,
            formationsAvecLeurAffinite = suggestions.formations,
        )
        val idsFormations = formationRechercheesTriees.map { it.id }
        val hateoas =
            hateoasBuilder.creerHateoas(
                liste = idsFormations,
                numeroDePageActuelle = request.numeroDePage,
                tailleLot = TAILLE_LOT_RECHERCHE_DETAILLEE,
            )
        val formations =
            recupererFichesFormationsService.recupererFichesFormationPourProfil(
                profilEleve = profilEleve,
                suggestionsPourUnProfil = suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(profilEleve),
                idsFormations = hateoas.listeCoupee,
                obsoletesInclus = false,
            )
        return creerFormationsAvecExplicationsDTO(formations, hateoas)
    }

    @PostMapping("/{idformation}")
    @Operation(
        summary = "Récupération d'une formation, mode détaillé",
        description =
            "A partir de l'identifiant d'une fiche formation, récupère toutes les informations nécessaires à l'affichage de " +
                "la fiche formation, y compris la liste des explications sur la raison de cette suggestion.",
    )
    fun getFormation(
        @RequestBody request: GetFormationDTO,
        ): FormationAvecExplicationsDTO {
        val profilEleve = when{
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
                    "Ces informations sont personnalisées en fonction du profil transmis"
                    ,
    )
    fun getFichesFormations(
        @RequestBody request: GetFichesFormationsDTO,
    ): FormationsAvecExplicationsDTO {
        val profilEleve = when{
            request.profil == null -> recupererEleveAvecProfilExistant() ?: AvecProfilExistant("")
            else -> AvecProfilExistant("")
        }
        val hateoas =
            hateoasBuilder.creerHateoas(
                liste = request.ids,
                numeroDePageActuelle = request.numeroDePage,
                tailleLot = TAILLE_LOT_FORMATIONS,
            )
        val formations = recupererFichesFormationsService.recupererFichesFormationPourProfil(
                        profilEleve = profilEleve,
                        suggestionsPourUnProfil = suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(profilEleve),
                        idsFormations = hateoas.listeCoupee,
                        obsoletesInclus = true,
                    )
        return creerFormationsAvecExplicationsDTO(formations, hateoas)
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
        val dto = FormationsCourtesDTO(formations = formations.map { FormationCourteDTO(it) })
        dto.ajouterHateoas(hateoas)
        return dto
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun recupererLesFormationsAssocieesALaRecherche(recherche: String): Map<FormationCourte, Int> {
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

    private fun <T> creerFormationsAvecExplicationsDTO(
        formations: List<FicheFormation>,
        hateoas: Hateoas<T>,
    ): FormationsAvecExplicationsDTO {
        val dto = FormationsAvecExplicationsDTO(formations = formations.map { FormationAvecExplicationsDTO(it) })
        dto.ajouterHateoas(hateoas)
        return dto
    }

    companion object {
        private const val TAILLE_LOT_RECHERCHE_DETAILLEE = 30
        private const val TAILLE_LOT_RECHERCHE_SUCCINCTE = 30
        private const val TAILLE_LOT_SUGGESTIONS_FORMATIONS = 30
        private const val TAILLE_LOT_FORMATIONS = 30
        private const val TAILLE_MINIMUM_RECHERCHE = 2
        private const val TAILLE_MAXIMAL_RECHERCHE = 150
    }
}
