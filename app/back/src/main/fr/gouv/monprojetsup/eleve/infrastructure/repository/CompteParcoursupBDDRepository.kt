package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.eleve.domain.port.CompteParcoursupRepository
import org.slf4j.Logger
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Repository
class CompteParcoursupBDDRepository(
    private val compteParcoursupJPARepository: CompteParcoursupJPARepository,
    private val logger: Logger,
) : CompteParcoursupRepository {
    @Transactional(readOnly = false)
    override fun recupererIdCompteParcoursup(idEleve: UUID): Int? {
        return compteParcoursupJPARepository.findById(idEleve).getOrElse {
            logger.warn("L'élève $idEleve n'a pas relié son compte Parcoursup à son compte MonProjetSup")
            null
        }?.idParcoursup
    }
}
