package fr.gouv.monprojetsup.commun.hateoas.usecase

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.hateoas.domain.PaginationConstants.NUMERO_PREMIERE_PAGE
import fr.gouv.monprojetsup.commun.hateoas.domain.entity.Hateoas
import org.springframework.stereotype.Component
import kotlin.jvm.Throws
import kotlin.math.min
import kotlin.math.roundToInt

@Component
class HateoasBuilder {
    @Throws(MonProjetSupBadRequestException::class)
    fun <T> creerHateoas(
        liste: List<T>,
        numeroDePageActuelle: Int,
        tailleLot: Int,
    ): Hateoas<T> {
        val dernierePage = (liste.size / tailleLot.toFloat()).roundToInt() + NUMERO_PREMIERE_PAGE
        if (numeroDePageActuelle > dernierePage) {
            throw MonProjetSupBadRequestException(
                code = "PAGE_DEMANDEE_INXISTANTE",
                msg = "La page $numeroDePageActuelle n'existe pas. Veuillez en donner une entre $NUMERO_PREMIERE_PAGE et $dernierePage",
            )
        }
        val pageSuivante = if (dernierePage == numeroDePageActuelle) null else numeroDePageActuelle + 1
        return Hateoas(
            premierePage = NUMERO_PREMIERE_PAGE,
            dernierePage = dernierePage,
            pageActuelle = numeroDePageActuelle,
            pageSuivante = pageSuivante,
            listeCoupee = recupererSousListe(liste = liste, numeroDePageActuelle = numeroDePageActuelle, tailleLot = tailleLot),
        )
    }

    private fun <T> recupererSousListe(
        liste: List<T>,
        numeroDePageActuelle: Int,
        tailleLot: Int,
    ): List<T> {
        val deLIndex = (numeroDePageActuelle - 1) * tailleLot
        val aLIndex = numeroDePageActuelle * tailleLot
        val nombreDeFormations = liste.size
        if (deLIndex > nombreDeFormations) return emptyList()
        return liste.subList(deLIndex, min(aLIndex, liste.size))
    }
}
