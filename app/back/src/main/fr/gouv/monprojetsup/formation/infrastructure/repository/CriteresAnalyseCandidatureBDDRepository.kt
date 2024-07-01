package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.domain.entity.CriteresAnalyseCandidature
import fr.gouv.monprojetsup.formation.domain.port.CriteresAnalyseCandidatureRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class CriteresAnalyseCandidatureBDDRepository(
    val criteresAnalyseCandidatureJPARepository: CriteresAnalyseCandidatureJPARepository,
) : CriteresAnalyseCandidatureRepository {
    @Transactional(readOnly = true)
    override fun recupererLesCriteresDUneFormation(valeursCriteresAnalyseCandidature: List<Int>): List<CriteresAnalyseCandidature> {
        val criteresAnalyseCandidature = criteresAnalyseCandidatureJPARepository.findAll()
        return valeursCriteresAnalyseCandidature.mapIndexedNotNull { index: Int, valeur: Int ->
            criteresAnalyseCandidature.firstOrNull { it.index == index }?.let {
                CriteresAnalyseCandidature(nom = it.nom, pourcentage = valeur)
            }
        }
    }
}
