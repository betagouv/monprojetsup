package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import org.springframework.stereotype.Service

@Service
class RecupererFormationsService(
    private val formationRepository: FormationRepository,
) {
    fun recupererFormations(idsFormations: List<String>): List<FormationCourte> {
        return formationRepository.recupererLesNomsDesFormations(idsFormations)
    }
}
