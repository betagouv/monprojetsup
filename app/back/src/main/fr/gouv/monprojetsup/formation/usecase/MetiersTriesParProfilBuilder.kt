package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.metier.domain.entity.Metier
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
class MetiersTriesParProfilBuilder(
    val logger: Logger,
) {
    fun trierMetiersParAffinites(
        metiers: List<Metier>,
        idsMetierTriesParAffinite: List<String>,
    ): List<Metier> {
        val metiersSansAffinites = mutableListOf<String>()
        val idMetiersEtLeurIndex = idsMetierTriesParAffinite.mapIndexed { index, idMetier -> Pair(idMetier, index) }.toMap()
        val metiersTries =
            metiers.sortedBy {
                idMetiersEtLeurIndex[it.id] ?: loggerErreurEtPlacerALaFin(idMetiersEtLeurIndex.size, it.id, metiersSansAffinites)
            }
        metiersSansAffinites.distinct().forEach {
            logger.error("Le metier $it n'est pas retourné dans la liste des métiers triés par affinité par l'API")
        }
        return metiersTries
    }

    private fun loggerErreurEtPlacerALaFin(
        tailleListe: Int,
        idMetier: String,
        metiersSansAffinites: MutableList<String>,
    ): Int {
        metiersSansAffinites.add(idMetier)
        return tailleListe + idMetier.hashCode()
    }
}
