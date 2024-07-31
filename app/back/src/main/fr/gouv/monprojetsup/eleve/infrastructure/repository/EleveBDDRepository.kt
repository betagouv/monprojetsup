package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.eleve.domain.port.EleveRepository
import fr.gouv.monprojetsup.eleve.infrastructure.entity.ProfilEleveEntity
import jakarta.persistence.EntityManager
import org.slf4j.Logger
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Repository
class EleveBDDRepository(
    private val entityManager: EntityManager,
    private val eleveJPARepository: EleveJPARepository,
    private val logger: Logger,
) : EleveRepository {
    @Transactional(readOnly = true)
    override fun recupererUnEleve(id: String): ProfilEleve {
        val eleve = eleveJPARepository.findById(UUID.fromString(id)).getOrNull()?.toProfilEleve() ?: ProfilEleve.Inconnu(id)
        return eleve
    }

    @Transactional(readOnly = false)
    override fun creerUnEleve(id: String): ProfilEleve.Identifie {
        val uuid = UUID.fromString(id)
        if (eleveJPARepository.existsById(uuid)) {
            logger.warn("L'élève $id a voulu être crée alors qu'il existe déjà en base")
            return eleveJPARepository.findById(UUID.fromString(id)).get().toProfilEleve()
        } else {
            val entity = ProfilEleveEntity()
            entity.id = UUID.fromString(id)
            val entitySauvegarde = eleveJPARepository.save(entity)
            return entitySauvegarde.toProfilEleve()
        }
    }

    @Throws(MonProjetSupNotFoundException::class)
    @Transactional(readOnly = false)
    override fun mettreAJourUnProfilEleve(profilEleve: ProfilEleve.Identifie) {
        try {
            entityManager.merge(ProfilEleveEntity(profilEleve))
        } catch (exception: Exception) {
            throw MonProjetSupNotFoundException("ELEVE_NON_CREE", "L'élève ${profilEleve.id} n'a pas été crée en base")
        }
    }
}
