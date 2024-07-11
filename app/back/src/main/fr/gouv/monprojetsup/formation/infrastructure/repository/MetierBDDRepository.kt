package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.domain.entity.MetierDetaille
import fr.gouv.monprojetsup.formation.domain.port.MetierRepository
import org.slf4j.Logger
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class MetierBDDRepository(
    val metierJPARepository: MetierJPARepository,
    val logger: Logger,
) : MetierRepository {
    @Transactional(readOnly = true)
    override fun recupererLesMetiersDetailles(ids: List<String>): List<MetierDetaille> {
        val metiers = metierJPARepository.findAllByIdIn(ids)
        return ids.mapNotNull { idMetier ->
            val metier = metiers.firstOrNull { it.id == idMetier }
            if (metier == null) {
                logger.error("Le métier $idMetier n'est pas présent en base")
            }
            metier?.toMetierDetaille()
        }
    }
}
