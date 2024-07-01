package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.domain.entity.Domaine
import fr.gouv.monprojetsup.formation.domain.port.DomaineRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class DomaineBDDRepository(
    val domaineJPARepository: DomaineJPARepository,
) : DomaineRepository {
    @Transactional(readOnly = true)
    override fun recupererLesDomaines(ids: List<String>): List<Domaine> {
        return domaineJPARepository.findAllByIdIn(ids).map { it.toDomaine() }
    }
}
