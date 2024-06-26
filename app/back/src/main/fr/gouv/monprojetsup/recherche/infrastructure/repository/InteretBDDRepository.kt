package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.recherche.domain.entity.InteretSousCategorie
import fr.gouv.monprojetsup.recherche.domain.port.InteretRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class InteretBDDRepository(
    val interetJPARepository: InteretJPARepository,
) : InteretRepository {
    @Transactional(readOnly = true)
    override fun recupererLesSousCategoriesDInterets(idsInterets: List<String>): List<InteretSousCategorie> {
        return interetJPARepository.findAllByIdIn(idsInterets).map { it.sousCategorie.toInteretSousCategorie() }.distinct()
    }
}
