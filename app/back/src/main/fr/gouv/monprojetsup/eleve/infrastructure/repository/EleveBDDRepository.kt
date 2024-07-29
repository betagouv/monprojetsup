package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.eleve.domain.port.EleveRepository
import fr.gouv.monprojetsup.eleve.infrastructure.entity.ProfilEleveEntity
import org.slf4j.Logger
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class EleveBDDRepository(
    private val eleveJPARepository: EleveJPARepository,
    private val logger: Logger,
) : EleveRepository {
    @Throws(MonProjetSupNotFoundException::class)
    override fun recupererUnEleve(id: String): ProfilEleve {
        val eleveEntity =
            eleveJPARepository.findById(UUID.fromString(id)).orElseThrow {
                MonProjetSupNotFoundException(code = "ELEVE_SANS_COMPTE", msg = "L'élève n'a pas de compte")
            }
        return eleveEntity.toProfilEleve()
    }

    override fun creerUnEleve(id: String): ProfilEleve {
        val uuid = UUID.fromString(id)
        if (eleveJPARepository.existsById(uuid)) {
            logger.warn("L'élève $id a voulu être crée alors qu'il existe déjà en base")
            return recupererUnEleve(id)
        } else {
            val entity = ProfilEleveEntity()
            entity.id = UUID.fromString(id)
            val entitySauvegarde = eleveJPARepository.save(entity)
            return entitySauvegarde.toProfilEleve()
        }
    }
}
