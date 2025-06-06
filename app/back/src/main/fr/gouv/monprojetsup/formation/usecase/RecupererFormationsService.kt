package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RecupererFormationsService(
    private val formationRepository: FormationRepository,
) {
    @Transactional(readOnly = true)
    fun recupererFormations(idsFormations: List<String>): List<FormationCourte> {
        return formationRepository.recupererLesNomsDesFormations(idsFormations)
    }
}
