package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.eleve.domain.port.CompteParcoursupRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Repository
class CompteParcoursupBDDRepository(
    private val compteParcoursupJPARepository: CompteParcoursupJPARepository,
) : CompteParcoursupRepository {
    @Transactional(readOnly = false)
    override fun recupererIdCompteParcoursup(idEleve: String): Int? {
        return compteParcoursupJPARepository.findById(idEleve).getOrNull()?.idParcoursup
    }
}
