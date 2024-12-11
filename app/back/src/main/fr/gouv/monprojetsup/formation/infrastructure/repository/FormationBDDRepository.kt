package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.formation.domain.entity.Formation
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.infrastructure.entity.FormationDetailleeEntity
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrElse

@Repository
class FormationBDDRepository(
    val formationJPARepository: FormationJPARepository,
    val formationDetailleeJPARepository: FormationDetailleeJPARepository,
    val logger: MonProjetSupLogger,
) : FormationRepository {
    @Throws(MonProjetSupIllegalStateErrorException::class, MonProjetSupNotFoundException::class)
    override fun recupererUneFormation(idFormation: String): Formation {
        val formation: FormationDetailleeEntity =
            formationDetailleeJPARepository.findById(idFormation).getOrElse {
                throw MonProjetSupNotFoundException(
                    code = "RECHERCHE_FORMATION",
                    msg = "La formation $idFormation n'existe pas",
                )
            }
        return formation.toFormation()
    }

    override fun recupererLesFormations(
        idsFormations: List<String>,
        obsoletesInclus: Boolean,
    ): List<Formation> {
        val formations =
            if (obsoletesInclus) {
                formationDetailleeJPARepository.findAllByIdIn(ids = idsFormations)
            } else {
                formationDetailleeJPARepository.findAllByIdInAndObsolete(id = idsFormations, obsolete = false)
            }
        return idsFormations.mapNotNull { idFormation ->
            val formation = formations.firstOrNull { formation -> formation.id == idFormation }
            if (formation == null) {
                logguerFormationInconnue(idFormation)
            }
            formation?.toFormation()
        }
    }

    override fun recupererLesNomsDesFormations(idsFormations: List<String>): List<FormationCourte> {
        val formations = formationJPARepository.findAllByIdIn(idsFormations)
        return formations.map { it.toFormationCourte() }
    }

    override fun recupererIdsFormationsInexistantes(ids: List<String>): List<String> {
        val existingIds = formationJPARepository.findExistingIds(ids)
        return ids.filterNot { existingIds.contains(it) }
    }

    private fun logguerFormationInconnue(idFormation: String) {
        logger.error(
            type = "FORMATION_ABSENTE_BDD",
            message = "La formation $idFormation n'est pas présente en base",
            parametres = mapOf("idFormationAbsente" to idFormation),
        )
    }
}
