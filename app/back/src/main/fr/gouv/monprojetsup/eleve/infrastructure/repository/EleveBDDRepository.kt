package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.eleve.domain.port.EleveRepository
import fr.gouv.monprojetsup.eleve.infrastructure.entity.ProfilEleveEntity
import org.slf4j.Logger
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Repository
class EleveBDDRepository(
    private val eleveJPARepository: EleveJPARepository,
    private val logger: Logger,
) : EleveRepository {
    @Transactional(readOnly = true)
    override fun recupererUnEleve(id: UUID): ProfilEleve {
        val eleve = eleveJPARepository.findById(id).getOrNull()?.toProfilEleve() ?: ProfilEleve.SansCompte(id)
        return eleve
    }

    @Transactional(readOnly = false)
    override fun creerUnEleve(id: UUID): ProfilEleve.Identifie {
        if (eleveJPARepository.existsById(id)) {
            logger.warn("L'élève $id a voulu être crée alors qu'il existe déjà en base")
            return eleveJPARepository.findById(id).get().toProfilEleve()
        } else {
            val entity = ProfilEleveEntity()
            entity.id = id
            val entitySauvegarde = eleveJPARepository.save(entity)
            return entitySauvegarde.toProfilEleve()
        }
    }

    @Throws(MonProjetSupNotFoundException::class)
    @Transactional(readOnly = false)
    override fun mettreAJourUnProfilEleve(profilEleve: ProfilEleve.Identifie) {
        try {
            eleveJPARepository.getReferenceById(profilEleve.id)
            eleveJPARepository.save(ProfilEleveEntity(profilEleve))
        } catch (exception: Exception) {
            throw MonProjetSupNotFoundException("ELEVE_NON_CREE", "L'élève ${profilEleve.id} n'a pas été crée en base")
        }
    }
}
