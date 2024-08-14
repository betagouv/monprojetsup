package fr.gouv.monprojetsup.metier.application.controller

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.hateoas.domain.PaginationConstants.PARAMETRE_NUMERO_PAGE
import fr.gouv.monprojetsup.commun.hateoas.usecase.HateoasBuilder
import fr.gouv.monprojetsup.metier.application.dto.MetierCourtDTO
import fr.gouv.monprojetsup.metier.application.dto.MetierDTO
import fr.gouv.monprojetsup.metier.application.dto.MetiersCourtsDTO
import fr.gouv.monprojetsup.metier.application.dto.MetiersDTO
import fr.gouv.monprojetsup.metier.usecase.RechercherMetiersService
import fr.gouv.monprojetsup.metier.usecase.RecupererMetiersService
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("api/v1/metiers")
@RestController
class MetierController(
    val rechercherMetiersService: RechercherMetiersService,
    val recupererMetiersService: RecupererMetiersService,
    val hateoasBuilder: HateoasBuilder,
) {
    @GetMapping("/recherche/succincte")
    fun getRechercheMetierSuccincte(
        @RequestParam recherche: String,
        @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "1", value = PARAMETRE_NUMERO_PAGE) numeroDePage: Int,
    ): MetiersCourtsDTO {
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
        val metiers =
            rechercherMetiersService.rechercherMetiers(
                recherche = recherche,
                tailleMinimumRecherche = TAILLE_MINIMUM_RECHERCHE,
            )
        val hateoas = hateoasBuilder.creerHateoas(metiers, numeroDePage, TAILLE_LOT_RECHERCHE_SUCCINCTE)
        val dto = MetiersCourtsDTO(metiers = metiers.map { MetierCourtDTO(it) })
        dto.ajouterHateoas(hateoas)
        return dto
    }

    @GetMapping
    fun getMetiers(
        @RequestParam ids: List<String>,
        @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "1", value = PARAMETRE_NUMERO_PAGE) numeroDePage: Int,
    ): MetiersDTO {
        val metiers = recupererMetiersService.recupererMetiers(ids)
        val hateoas = hateoasBuilder.creerHateoas(metiers, numeroDePage, TAILLE_LOT_ID)
        val dto = MetiersDTO(metiers.map { MetierDTO(it) })
        dto.ajouterHateoas(hateoas)
        return dto
    }

    companion object {
        private const val TAILLE_LOT_RECHERCHE_SUCCINCTE = 30
        private const val TAILLE_LOT_ID = 30
        private const val TAILLE_MINIMUM_RECHERCHE = 2
        private const val TAILLE_MAXIMAL_RECHERCHE = 50
    }
}
