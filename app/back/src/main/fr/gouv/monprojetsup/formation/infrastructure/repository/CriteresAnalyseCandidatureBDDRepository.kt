package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.domain.entity.CritereAnalyseCandidature
import fr.gouv.monprojetsup.formation.domain.entity.Formation
import fr.gouv.monprojetsup.formation.domain.port.CriteresAnalyseCandidatureRepository
import fr.gouv.monprojetsup.formation.infrastructure.entity.CritereAnalyseCandidatureEntity
import org.springframework.stereotype.Repository

@Repository
class CriteresAnalyseCandidatureBDDRepository(
    val criteresAnalyseCandidatureJPARepository: CriteresAnalyseCandidatureJPARepository,
) : CriteresAnalyseCandidatureRepository {
    override fun recupererLesCriteresDUneFormation(valeursCriteresAnalyseCandidature: List<Int>): List<CritereAnalyseCandidature> {
        val criteresAnalyseCandidature = criteresAnalyseCandidatureJPARepository.findAll()
        return mapCritereAnalyseCanditature(valeursCriteresAnalyseCandidature, criteresAnalyseCandidature)
    }

    override fun recupererLesCriteresDeFormations(formations: List<Formation>): Map<String, List<CritereAnalyseCandidature>> {
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
