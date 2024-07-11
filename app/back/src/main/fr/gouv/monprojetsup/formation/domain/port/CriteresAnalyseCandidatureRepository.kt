package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.formation.domain.entity.CritereAnalyseCandidature
import fr.gouv.monprojetsup.formation.domain.entity.Formation

interface CriteresAnalyseCandidatureRepository {
    fun recupererLesCriteresDUneFormation(valeursCriteresAnalyseCandidature: List<Int>): List<CritereAnalyseCandidature>

    fun recupererLesCriteresDeFormations(formations: List<Formation>): Map<String, List<CritereAnalyseCandidature>>
}
