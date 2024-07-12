package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.domain.entity.CategorieDomaine
import fr.gouv.monprojetsup.referentiel.domain.entity.Domaine
import fr.gouv.monprojetsup.referentiel.domain.port.DomaineRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class DomaineBDDRepository(
    val domaineJPARepository: DomaineJPARepository,
    val categorieDomaineJPARepository: CategorieDomaineJPARepository,
) : DomaineRepository {
    @Transactional(readOnly = true)
    override fun recupererLesDomaines(ids: List<String>): List<Domaine> {
        return domaineJPARepository.findAllByIdIn(ids).map { it.toDomaine() }
    }

    override fun recupererTousLesDomainesEtLeursCategories(): Map<CategorieDomaine, List<Domaine>> {
        return categorieDomaineJPARepository.findAll().associate { it.toCategorieDomaine() to it.domaines.map { it.toDomaine() } }
    }
}
