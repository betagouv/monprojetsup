package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.commun.clock.MonProjetSupClock
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.eleve.domain.entity.ActionEleve
import fr.gouv.monprojetsup.eleve.domain.entity.Trace
import fr.gouv.monprojetsup.eleve.domain.port.TraceRepository
import fr.gouv.monprojetsup.eleve.infrastructure.entity.TraceEntity
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import org.springframework.stereotype.Repository

@Repository
class TraceBDDRepository(
    private val traceJPARepository: TraceJPARepository,
    private val logger: MonProjetSupLogger,
    private val clock: MonProjetSupClock,
) : TraceRepository {
    override fun ajouterTrace(
        idEleve: String,
        actionEleve: ActionEleve,
        param1: String?,
        param2: String?,
    ) {
        val entity =
            TraceEntity(
                idEleve = idEleve,
                actionEleve = actionEleve,
                param1 = param1,
                param2 = param2,
                tsp = clock.dateTimeActuelle(),
            )
        try {
            traceJPARepository.saveAndFlush(entity)
            logger.info(type = "TRACE_ENREGISTREE", message = "Un élève a effectué une action")
        } catch (e: Exception) {
            val exception =
                MonProjetSupInternalErrorException(
                    code = "SAVE_TRACE_FAILED",
                    msg = "Echec de sauvagarde de la trace idEleve $idEleve action $actionEleve",
                    origine = e,
                )
            logger.error(exception.code, exception.message, exception.origine)
        }
    }

    override fun getTraces(idEleve: String): List<Trace> {
        return traceJPARepository.findByIdEleve(idEleve).map { it.toTrace() }
    }
}
