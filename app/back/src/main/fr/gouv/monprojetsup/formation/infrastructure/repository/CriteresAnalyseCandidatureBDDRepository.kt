package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.domain.entity.CritereAnalyseCandidature
import fr.gouv.monprojetsup.formation.domain.entity.FormationDetaillee
import fr.gouv.monprojetsup.formation.domain.port.CriteresAnalyseCandidatureRepository
import fr.gouv.monprojetsup.formation.infrastructure.entity.CritereAnalyseCandidatureEntity
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class CriteresAnalyseCandidatureBDDRepository(
    val criteresAnalyseCandidatureJPARepository: CriteresAnalyseCandidatureJPARepository,
) : CriteresAnalyseCandidatureRepository {
    @Transactional(readOnly = true)
    override fun recupererLesCriteresDUneFormation(valeursCriteresAnalyseCandidature: List<Int>): List<CritereAnalyseCandidature> {
        val criteresAnalyseCandidature = criteresAnalyseCandidatureJPARepository.findAll()
        return mapCritereAnalyseCanditature(valeursCriteresAnalyseCandidature, criteresAnalyseCandidature)
    }

    @Transactional(readOnly = true)
    override fun recupererLesCriteresDeFormations(formations: List<FormationDetaillee>): Map<String, List<CritereAnalyseCandidature>> {
        val criteresAnalyseCandidature = criteresAnalyseCandidatureJPARepository.findAll()
        return formations.associate {
            it.id to
                mapCritereAnalyseCanditature(
                    valeursCriteresAnalyseCandidature = it.valeurCriteresAnalyseCandidature,
                    criteresAnalyseCandidature = criteresAnalyseCandidature,
                )
        }
    }

    private fun mapCritereAnalyseCanditature(
        valeursCriteresAnalyseCandidature: List<Int>,
        criteresAnalyseCandidature: List<CritereAnalyseCandidatureEntity>,
    ) = valeursCriteresAnalyseCandidature.mapIndexedNotNull { index: Int, valeur: Int ->
        criteresAnalyseCandidature.firstOrNull { it.index == index }?.let {
            CritereAnalyseCandidature(nom = it.nom, pourcentage = valeur)
        }
    }
}
