package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.domain.entity.Interet
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretCategorie
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie
import fr.gouv.monprojetsup.referentiel.domain.port.InteretRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class InteretBDDRepository(
    val interetJPARepository: InteretJPARepository,
    val interetSousCategorieJPARepository: InteretSousCategorieJPARepository,
    val interetCategorieJPARepository: InteretCategorieJPARepository,
) : InteretRepository {
    @Transactional(readOnly = true)
    override fun recupererLesSousCategories(idsSousCategoriesInterets: List<String>): List<InteretSousCategorie> {
        return interetSousCategorieJPARepository.findAllById(idsSousCategoriesInterets).map { it.toInteretSousCategorie() }
    }

    @Transactional(readOnly = true)
    override fun recupererLesInteretsDeSousCategories(idsSousCategoriesInterets: List<String>): List<Interet> {
        return interetJPARepository.findAllByIdSousCategorieIn(idsSousCategoriesInterets).map { it.toInteret() }
    }

    @Transactional(readOnly = true)
    override fun recupererToutesLesCategoriesEtLeursSousCategoriesDInterets(): Map<InteretCategorie, List<InteretSousCategorie>> {
        return interetCategorieJPARepository.findAll().associate {
            it.toInteretCategorie() to it.sousCategories.map { it.toInteretSousCategorie() }
        }
    }

    @Transactional(readOnly = true)
    override fun recupererIdsCentresInteretsInexistants(ids: List<String>): List<String> {
        val existingIds = interetSousCategorieJPARepository.findExistingIds(ids)
        return ids.filterNot { existingIds.contains(it) }
    }
}
