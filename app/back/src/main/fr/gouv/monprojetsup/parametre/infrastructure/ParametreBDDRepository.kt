package fr.gouv.monprojetsup.parametre.infrastructure

import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.parametre.domain.entity.Parametre
import fr.gouv.monprojetsup.parametre.domain.port.ParametreRepository
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
class ParametreBDDRepository(
    private val parametreJPARepository: ParametreJPARepository,
    private val logger: MonProjetSupLogger,
) : ParametreRepository {
    override fun estActif(parametre: Parametre): Boolean {
        return when (val statut = parametreJPARepository.findById(parametre).getOrNull()?.statut) {
            null -> {
                logger.error(type = "PARAMETRE_MANQUANT", message = "Paramètre ${parametre.name} non trouvé en base")
                false
            }
            else -> statut
        }
    }
}
