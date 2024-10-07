package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.commun.clock.MonProjetSupClock
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.eleve.domain.port.CompteParcoursupRepository
import fr.gouv.monprojetsup.eleve.infrastructure.entity.CompteParcoursupEntity
import org.slf4j.Logger
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Repository
class CompteParcoursupBDDRepository(
    private val compteParcoursupJPARepository: CompteParcoursupJPARepository,
    private val logger: Logger,
    private val clock: MonProjetSupClock,
) : CompteParcoursupRepository {
    @Transactional(readOnly = false)
    override fun enregistrerIdCompteParcoursup(
        idEleve: String,
        idParcoursup: Int,
    ) {
        val entite =
            CompteParcoursupEntity(
                idEleve = idEleve,
                idParcoursup = idParcoursup,
                dateMiseAJour = clock.dateActuelle(),
            )
        try {
            compteParcoursupJPARepository.saveAndFlush(entite)
        } catch (e: Exception) {
            val exception =
                MonProjetSupNotFoundException(
                    code = "ELEVE_NOT_FOUND",
                    msg = "L'élève avec l'id $idEleve tente de sauvegarder son id parcoursup mais il n'est pas encore sauvegardé en BDD",
                )
            logger.error(exception.message, exception)
            throw exception
        }
    }

    @Transactional(readOnly = true)
    override fun recupererIdCompteParcoursup(idEleve: String): Int? {
        return compteParcoursupJPARepository.findById(idEleve).getOrNull()?.idParcoursup
    }
}
