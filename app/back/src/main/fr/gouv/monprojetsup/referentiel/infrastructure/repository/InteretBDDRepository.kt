package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.domain.entity.Interet
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretCategorie
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie
import fr.gouv.monprojetsup.referentiel.domain.port.InteretRepository
import fr.gouv.monprojetsup.referentiel.infrastructure.entity.InteretEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class InteretBDDRepository(
    private val entityManager: EntityManager,
    private val interetSousCategorieJPARepository: InteretSousCategorieJPARepository,
    private val interetCategorieJPARepository: InteretCategorieJPARepository,
) : InteretRepository {
    override fun recupererLesSousCategories(idsSousCategoriesInterets: List<String>): List<InteretSousCategorie> {
        return interetSousCategorieJPARepository.findAllById(idsSousCategoriesInterets).map { it.toInteretSousCategorie() }
    }

    override fun recupererLesInteretsDeSousCategories(idsSousCategoriesInterets: List<String>): List<Interet> {
        return findAllByIdSousCategorieIn(idsSousCategoriesInterets).map { it.toInteret() }
    }

    override fun recupererToutesLesCategoriesEtLeursSousCategoriesDInterets(): Map<InteretCategorie, List<InteretSousCategorie>> {
        return interetCategorieJPARepository.findAll().associate {
            it.toInteretCategorie() to it.sousCategories.map { it.toInteretSousCategorie() }
        }
    }

    override fun recupererIdsCentresInteretsInexistants(ids: List<String>): List<String> {
        val existingIds = interetSousCategorieJPARepository.findExistingIds(ids)
        return ids.filterNot { existingIds.contains(it) }
    }

    private fun findAllByIdSousCategorieIn(ids: List<String>): List<InteretEntity> {
        return entityManager.createQuery(
            """
            SELECT i
            FROM InteretEntity i
            JOIN InteretSousCategorieEntity sc ON i.idSousCategorie = sc.id
            WHERE i.idSousCategorie IN :ids
            """,
            InteretEntity::class.java,
        ).setParameter("ids", ids).resultList
    }
}
