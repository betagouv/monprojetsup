package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.CritereAnalyseCandidature
import fr.gouv.monprojetsup.formation.domain.entity.FormationDetaillee
import fr.gouv.monprojetsup.formation.domain.port.CriteresAnalyseCandidatureRepository
import org.springframework.stereotype.Service

@Service
class CritereAnalyseCandidatureService(
    val criteresAnalyseCandidatureRepository: CriteresAnalyseCandidatureRepository,
) {
    fun recupererCriteresAnalyseCandidature(formation: FormationDetaillee): List<CritereAnalyseCandidature> {
        return criteresAnalyseCandidatureRepository.recupererLesCriteresDUneFormation(
            valeursCriteresAnalyseCandidature = formation.valeurCriteresAnalyseCandidature,
        ).filterNot { it.pourcentage == 0 }
    }

    fun recupererCriteresAnalyseCandidature(formations: List<FormationDetaillee>): Map<String, List<CritereAnalyseCandidature>> {
        return criteresAnalyseCandidatureRepository.recupererLesCriteresDeFormations(
            formations,
        ).map { it.key to it.value.filterNot { critere -> critere.pourcentage == 0 } }.toMap()
    }
}
