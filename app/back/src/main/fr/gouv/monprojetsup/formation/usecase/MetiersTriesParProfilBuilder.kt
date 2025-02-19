package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.metier.domain.entity.Metier
import org.springframework.stereotype.Service

@Service
class MetiersTriesParProfilBuilder {
    fun trierMetiersParAffinites(
        metiers: List<Metier>,
        idsMetierTriesParAffinite: List<String>,
    ): List<Metier> {
        val idMetiersEtLeurIndex = idsMetierTriesParAffinite.mapIndexed { index, idMetier -> Pair(idMetier, index) }.toMap()
        return metiers.sortedBy {
                idMetiersEtLeurIndex[it.id] ?: metiers.size
            }
    }
}
