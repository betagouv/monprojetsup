package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.recherche.domain.entity.Interet
import fr.gouv.monprojetsup.recherche.domain.port.InteretRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class InteretBDDRepository(
    val interetJPARepository: InteretJPARepository,
) : InteretRepository {
    @Transactional(readOnly = true)
    override fun recupererLesInterets(ids: List<String>): List<Interet> {
        return interetJPARepository.findAllByIdIn(ids).map { it.toInteret() }
    }
}
