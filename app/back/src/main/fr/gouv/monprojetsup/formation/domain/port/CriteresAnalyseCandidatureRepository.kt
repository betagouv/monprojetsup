package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.formation.domain.entity.CriteresAnalyseCandidature

interface CriteresAnalyseCandidatureRepository {
    fun recupererLesCriteresDUneFormation(valeursCriteresAnalyseCandidature: List<Int>): List<CriteresAnalyseCandidature>
}
