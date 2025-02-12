package fr.gouv.monprojetsup.eleve.usecase

import fr.gouv.monprojetsup.eleve.domain.entity.Trace
import fr.gouv.monprojetsup.eleve.domain.port.TraceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AjoutTraceService(
    private val traceRepository: TraceRepository,
) {
    @Transactional(readOnly = false)
    fun ajouterTrace(
        idEleve: String,
        trace: Trace,
    ) {
        traceRepository.ajouterTrace(idEleve, trace.actionEleve, trace.param1, trace.param2)
    }
}
