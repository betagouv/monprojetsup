package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.formation.domain.entity.Formation
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.infrastructure.entity.FormationDetailleeEntity
import org.slf4j.Logger
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrElse

@Repository
class FormationBDDRepository(
    val formationJPARepository: FormationJPARepository,
    val formationDetailleeJPARepository: FormationDetailleeJPARepository,
    val logger: Logger,
) : FormationRepository {
    @Throws(MonProjetIllegalStateErrorException::class, MonProjetSupNotFoundException::class)
    @Transactional(readOnly = true)
    override fun recupererUneFormationAvecSesMetiers(idFormation: String): Formation {
        val formation: FormationDetailleeEntity =
            formationDetailleeJPARepository.findById(idFormation).getOrElse {
                throw MonProjetSupNotFoundException(
                    code = "RECHERCHE_FORMATION",
                    msg = "La formation $idFormation n'existe pas",
                )
            }
        return formation.toFormation()
    }

    @Transactional(readOnly = true)
    override fun recupererLesFormationsAvecLeursMetiers(idsFormations: List<String>): List<Formation> {
        val formations = formationDetailleeJPARepository.findAllByIdIn(idsFormations)
        return idsFormations.mapNotNull { idFormation ->
            val formation = formations.firstOrNull { formation -> formation.id == idFormation }
            if (formation == null) {
                logguerFormationInconnue(idFormation)
            }
            formation?.toFormation()
        }
    }

    @Transactional(readOnly = true)
    override fun recupererLesNomsDesFormations(idsFormations: List<String>): List<FormationCourte> {
        val formations = formationJPARepository.findAllByIdIn(idsFormations)
        return idsFormations.mapNotNull { idFormation ->
            val formation = formations.firstOrNull { formation -> formation.id == idFormation }
            if (formation == null) {
                logguerFormationInconnue(idFormation)
            }
            formation?.toFormationCourte()
        }
    }

    private fun logguerFormationInconnue(idFormation: String) {
        logger.error("La formation $idFormation n'est pas présente en base")
    }
}
