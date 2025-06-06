package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.commun.clock.MonProjetSupClock
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.eleve.domain.port.CompteParcoursupRepository
import fr.gouv.monprojetsup.eleve.infrastructure.entity.CompteParcoursupEntity
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
class CompteParcoursupBDDRepository(
    private val compteParcoursupJPARepository: CompteParcoursupJPARepository,
    private val logger: MonProjetSupLogger,
    private val clock: MonProjetSupClock,
) : CompteParcoursupRepository {
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
            logger.info(type = "ID_PARCOURSUP_ENREGISTRE", message = "Un élève a associé son compte MonProjetSup à son compte Parcoursup")
        } catch (e: Exception) {
            val exception =
                MonProjetSupNotFoundException(
                    code = "ELEVE_NOT_FOUND",
                    msg = "L'élève avec l'id $idEleve tente de sauvegarder son id parcoursup mais il n'est pas encore sauvegardé en BDD",
                    origine = e,
                )
            logger.error(exception.code, exception.message, exception.origine)
            throw exception
        }
    }

    override fun recupererIdCompteParcoursup(idEleve: String): Int? {
        return compteParcoursupJPARepository.findById(idEleve).getOrNull()?.idParcoursup
    }
}
