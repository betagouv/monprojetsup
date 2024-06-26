package fr.gouv.monprojetsup.recherche.domain.port

import fr.gouv.monprojetsup.recherche.domain.entity.CriteresAnalyseCandidature

interface CriteresAnalyseCandidatureRepository {
    fun recupererLesCriteresDUneFormation(valeursCriteresAnalyseCandidature: List<Int>): List<CriteresAnalyseCandidature>
}
