package fr.gouv.monprojetsup.metier.application.controller

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.metier.application.dto.MetierCourtDTO
import fr.gouv.monprojetsup.metier.application.dto.MetierDTO
import fr.gouv.monprojetsup.metier.application.dto.MetiersCourtsDTO
import fr.gouv.monprojetsup.metier.application.dto.MetiersDTO
import fr.gouv.monprojetsup.metier.usecase.RechercherMetiersService
import fr.gouv.monprojetsup.metier.usecase.RecupererMetiersService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("api/v1/metiers")
@RestController
class MetierController(
    val rechercherMetiersService: RechercherMetiersService,
    val recupererMetiersService: RecupererMetiersService,
) {
    @GetMapping("/recherche/succincte")
    fun getRechercheMetierSuccincte(
        @RequestParam recherche: String,
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
                nombreMaximaleDeMetier = NOMBRE_METIERS_MAXIMAL_RETOURNES,
                tailleMinimumRecherche = TAILLE_MINIMUM_RECHERCHE,
            )
        return MetiersCourtsDTO(
            metiers = metiers.map { MetierCourtDTO(it) },
        )
    }

    @GetMapping
    fun getMetiers(
        @RequestParam ids: List<String>,
    ): MetiersDTO {
        val metiers = recupererMetiersService.recupererMetiers(ids)
        return MetiersDTO(metiers.map { MetierDTO(it) })
    }

    companion object {
        private const val NOMBRE_METIERS_MAXIMAL_RETOURNES = 30
        private const val TAILLE_MINIMUM_RECHERCHE = 2
        private const val TAILLE_MAXIMAL_RECHERCHE = 50
    }
}
